package com.swp.GUI.Extras;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;

import com.gumse.PostProcessing.Framebuffer;
import com.gumse.gui.GUI;
import com.gumse.gui.GUIShader;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.mat4;
import com.gumse.maths.vec3;
import com.gumse.maths.vec4;
import com.gumse.model.VertexArrayObject;
import com.gumse.model.VertexBufferObject;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Output;

/**
 * AudioGUI wird dazu verwendet um 
 * eine Audio datei mittels einem GUI element 
 * abzuspielen und zu pausieren.
 */
public class AudioGUI extends RenderGUI
{
    private Box pBackground;
    private mat4 m4PlayPauseMatrix;
    private boolean bIsPlaying;
    private int iSourceID;

    private static VertexArrayObject pPauseVAO = null, pPlayVAO = null;

    private void initVAO()
    {
        if(pPlayVAO == null)
        {
            //ALC.create();

            pPlayVAO = new VertexArrayObject();
            VertexBufferObject pPlayVBO = new VertexBufferObject();

            pPlayVBO.setData(new ArrayList<Float>(Arrays.asList(new Float[] { 
                0.0f, 1.0f, 0.0f,
                1.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.0f,
            })));
            pPlayVAO.addAttribute(pPlayVBO, 0, 3, GL11.GL_FLOAT, 0, 0);

            pPauseVAO = new VertexArrayObject();
            VertexBufferObject pPauseVBO = new VertexBufferObject();

            float startGap  = 0.18f;
            float gapSize   = 0.5f;
            float thickness = 0.15f;

            pPauseVBO.setData(new ArrayList<Float>(Arrays.asList(new Float[] {
                startGap + 0.0f,       0.0f, 0.0f,
                startGap + thickness,  0.0f, 0.0f,
                startGap + 0.0f,       1.0f, 0.0f,

                startGap + 0.0f,       1.0f, 0.0f,
                startGap + thickness,  0.0f, 0.0f,
                startGap + thickness,  1.0f, 0.0f,


                startGap + gapSize + 0.0f,       0.0f, 0.0f,
                startGap + gapSize + thickness,  0.0f, 0.0f,
                startGap + gapSize + 0.0f,       1.0f, 0.0f,

                startGap + gapSize + 0.0f,       1.0f, 0.0f,
                startGap + gapSize + thickness,  0.0f, 0.0f,
                startGap + gapSize + thickness,  1.0f, 0.0f,
            })));
            pPauseVAO.addAttribute(pPauseVBO, 0, 3, GL11.GL_FLOAT, 0, 0);
        }
    }

    public AudioGUI(ivec2 pos, ivec2 size)
    {
        this.sTitle = "AudioGUI";
        this.vPos.set(pos);
        this.vSize.set(size);
        this.bIsPlaying = false;
        setColor(GUI.getTheme().accentColor);

        initVAO();

        pBackground = new Box(new ivec2(0, 0), new ivec2(100, 100));
        pBackground.setSizeInPercent(true, true);
        pBackground.setColor(GUI.getTheme().primaryColor);
        addElement(pBackground);


        onHover(null, Mouse.GUM_CURSOR_HAND);
        onEnter((RenderGUI gui) -> { setColor(vec4.sub(GUI.getTheme().accentColor, 0.02f)); });
        onLeave((RenderGUI gui) -> { setColor(GUI.getTheme().accentColor); });
        onClick((RenderGUI gui) -> { toggle(); });

        resize();
        reposition();
    }

    @Override
    protected void updateOnPosChange()
    {
        vec3 rot = new vec3(0,0,0);

        float size = vActualSize.y * 0.5f;
        mat4 model = new mat4();
        model.translate(new vec3(
            vActualPos.x + vActualSize.y * 0.25f, 
            Window.CurrentlyBoundWindow.getSize().y - vActualPos.y - vActualSize.y * 0.75f, 
            0));
        model.scale(new vec3(size, size, 1.0f));
        model.rotate(rot);
        model.transpose();
        
        m4PlayPauseMatrix = model;
    }

    @Override
    public void renderextra() 
    {
        pBackground.render();
        renderPlayPause();
    }

    private void renderPlayPause()
    {
        GUIShader.getShaderProgram().use();
        GUIShader.getShaderProgram().loadUniform("orthomat", Framebuffer.CurrentlyBoundFramebuffer.getScreenMatrix());
        GUIShader.getShaderProgram().loadUniform("transmat", m4PlayPauseMatrix);
        GUIShader.getShaderProgram().loadUniform("Uppercolor", v4Color);
        GUIShader.getShaderProgram().loadUniform("borderThickness", 0.0f);
        GUIShader.getShaderProgram().loadUniform("hasTexture", false);
        if(bIsPlaying)
        {
            pPauseVAO.bind();
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 12);
            pPauseVAO.unbind();
        }
        else
        {
            pPlayVAO.bind();
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
            pPlayVAO.unbind();
        }
        GUIShader.getShaderProgram().unuse();
    }

    public void play()
    {
        AL11.alSourcePlay(iSourceID);
        bIsPlaying = true;
    }

    public void stop()
    {
        AL11.alSourceStop(iSourceID);
        bIsPlaying = false;
    }

    public void toggle()
    {
        if(bIsPlaying)
            stop();
        else
            play();
    }

    public void loadAudio(InputStream audio)
    {
        if(audio == null)
        {
            Output.error("AudioGUI: Given audiostream is null");
            return;
        }

        //Data buffer
        int audiobuffer = AL11.alGenBuffers();
        WaveData data = WaveData.create(audio);
        AL11.alBufferData(audiobuffer, data.format, data.data, data.samplerate);
        data.dispose();

        //Source
        iSourceID = AL11.alGenSources();
        AL11.alSourcef(iSourceID, AL11.AL_GAIN, 1);
        AL11.alSourcef(iSourceID, AL11.AL_PITCH, 1);
        AL11.alSource3f(iSourceID, AL11.AL_POSITION, 0, 0, 0);
        AL11.alSourcei(iSourceID, AL11.AL_BUFFER, audiobuffer);
    }

    public void loadAudio(byte[] data)
    {
        if(data == null)
        {
            Output.error("AudioGUI: Given audiodata is null");
            return;
        }

        loadAudio(new ByteArrayInputStream(data));
    }
}