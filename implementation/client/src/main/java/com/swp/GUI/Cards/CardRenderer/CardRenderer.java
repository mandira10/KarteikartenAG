package com.swp.GUI.Cards.CardRenderer;

import org.lwjgl.opengl.GL30;

import com.gumse.PostProcessing.Framebuffer;
import com.gumse.basics.Camera;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.mat4;
import com.gumse.maths.vec3;
import com.gumse.maths.vec4;
import com.gumse.model.Model3D;
import com.gumse.shader.Shader;
import com.gumse.shader.ShaderProgram;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.textures.Texture;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;

public class CardRenderer extends RenderGUI
{
    private static Model3D pCardModel = null;
    private static ShaderProgram pCardShader = null;
    private static Camera pCamera = null;
    private static Framebuffer pFramebuffer = null;
    private Card pCard;
    Texture tex;

    public CardRenderer(Card card)
    {
        this.vSize = new ivec2(100,100);
        this.pCard = card;

        if(pCardModel == null)
        {
            pCardShader = new ShaderProgram();
            pCardShader.addShader(new Shader(Shader.SHADER_VERSION_STR + Toolbox.loadResourceAsString("shaders/card.vert", CardRenderer.class), Shader.TYPES.VERTEX_SHADER));
            pCardShader.addShader(new Shader(Shader.SHADER_VERSION_STR + Toolbox.loadResourceAsString("shaders/card.frag", CardRenderer.class), Shader.TYPES.FRAGMENT_SHADER));
            pCardShader.build("CardShader");
            pCardShader.addUniform("color");
            pCardShader.addUniform("hasTexture");
            pCardShader.addTexture("frontTexture", 0);
            pCardShader.addTexture("backTexture", 1);

            pCardModel = new Model3D(pCardShader);
            pCardModel.load("models/card.obj", CardRenderer.class);
            pCardModel.setPosition(new vec3(0,1,0));
            pCardModel.setScale(new vec3(1.5f));
            //pCardModel.setColor(new vec4(0.26f, 0.26f, 0.31f, 1.0f));
            pCardModel.setColor(new vec4(1.0f));

            pCamera = new Camera(90.0f);
            pCamera.setPosition(new vec3(0, 0, 10.0f));
            //pCamera.setProjectionMatrix(Window.CurrentlyBoundWindow.getScreenMatrix());

            pFramebuffer = new Framebuffer(Window.CurrentlyBoundWindow.getSize());
            pFramebuffer.addTextureAttachment();
            pCardModel.setTexture(pFramebuffer.getTexture());
            tex = new Texture("TestTex");
            tex.load("textures/orange-ket.png", CardRenderer.class);
            //pCardModel.setTexture(tex);
        }

        updateOnSizeChange();

        
        this.setSizeInPercent(true, true);
        reposition();
        resize();

        renderToTexture();
    }

    public void renderToTexture()
    {
        pFramebuffer.bind();
        CardTypesRenderer.render(pCard);
        pFramebuffer.unbind();
    }

    @Override
    public void updateOnSizeChange()
    {
        float aspect = Window.CurrentlyBoundWindow.getAspectRatio();
        //aspect = 1.0f;
        float distance = 1.0f;
        pCamera.setProjectionMatrix(mat4.ortho(aspect * distance / 2, distance * 0.5f, -aspect * distance / 2, -distance * 0.5f, 0.1f, 1000.0f));
    }

    @Override
    public void render()
    {
        if(bIsHidden)
            return;
        
        pCardShader.use();
        pCardShader.loadUniform("projectionMatrix", pCamera.getProjectionMatrix());
        pCardShader.loadUniform("viewMatrix", pCamera.getViewMatrix());
        tex.bind(1);
        
		GL30.glEnable(GL30.GL_DEPTH_TEST);
        pCardModel.render();
		GL30.glDisable(GL30.GL_DEPTH_TEST);
        pCardShader.unuse();


        renderchildren();
    }

    @Override
    public void update()
    {
        if(bIsHidden)
            return;
        if(isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_ALL_SIDES_RESIZE);
            if(isHoldingLeftClick())
            {
                ivec2 delta = Window.CurrentlyBoundWindow.getMouse().getPositionDelta();
                pCardModel.increaseRotation(new vec3(delta.y, delta.x, 0));
            }       
        }

        updatechildren();
    }
}