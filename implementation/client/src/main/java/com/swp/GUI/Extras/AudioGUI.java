package com.swp.GUI.Extras;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;

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
                0.0f,  0.0f, 0.0f,
                1.0f, -0.5f, 0.0f,
                0.0f, -1.0f, 0.0f,
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
                startGap + 0.0f,      -1.0f, 0.0f,

                startGap + 0.0f,      -1.0f, 0.0f,
                startGap + thickness,  0.0f, 0.0f,
                startGap + thickness, -1.0f, 0.0f,


                startGap + gapSize + 0.0f,       0.0f, 0.0f,
                startGap + gapSize + thickness,  0.0f, 0.0f,
                startGap + gapSize + 0.0f,      -1.0f, 0.0f,

                startGap + gapSize + 0.0f,      -1.0f, 0.0f,
                startGap + gapSize + thickness,  0.0f, 0.0f,
                startGap + gapSize + thickness, -1.0f, 0.0f,
            })));
            pPauseVAO.addAttribute(pPauseVBO, 0, 3, GL11.GL_FLOAT, 0, 0);
        }
    }

    public AudioGUI(ivec2 pos, ivec2 size, InputStream audio)
    {
        this.sTitle = "AudioGUI";
        this.vPos.set(pos);
        this.vSize.set(size);
        this.bIsPlaying = false;

        initVAO();

        pBackground = new Box(new ivec2(0, 0), new ivec2(100, 100));
        pBackground.setSizeInPercent(true, true);
        pBackground.setColor(GUI.getTheme().primaryColor);
        //pBackground.setCornerRadius(new vec4(10));
        addElement(pBackground);

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

        this.v4Color = GUI.getTheme().accentColor;



        resize();
        reposition();
    }

    @Override
    protected void updateOnPosChange()
    {
        vec3 rot = new vec3(0,0,0);

        float size = vActualSize.y * 0.5f;
        mat4 model = new mat4();
        ivec2 offset = new ivec2((int)(vActualSize.y * 0.25f));
        model.translate(new vec3(
            vActualPos.x + offset.x, 
            Window.CurrentlyBoundWindow.getSize().y - vActualPos.y - offset.y, 
            0));
        model.scale(new vec3(size, size, 1.0f));
        model.rotate(rot);
        model.transpose();
        
        m4PlayPauseMatrix = model;
    }

    @Override
    public void render() 
    {
        pBackground.render();
        renderPlayPause();
    }

    @Override
    public void update() 
    {
        if(pBackground.isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);
            v4Color = vec4.sub(GUI.getTheme().accentColor, 0.02f);

            if(pBackground.isClicked())
            {
                toggle();
            }
        }
        else
        {
            v4Color = GUI.getTheme().accentColor;
        }
    }

    private void renderPlayPause()
    {
        GUIShader.getShaderProgram().use();
        GUIShader.getShaderProgram().loadUniform("orthomat", Window.CurrentlyBoundWindow.getScreenMatrix());
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
}