package com.swp.GUI.Cards.CardRenderer;

import org.lwjgl.opengl.GL30;

import com.gumse.PostProcessing.Framebuffer;
import com.gumse.basics.Camera;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.mat4;
import com.gumse.maths.quat;
import com.gumse.maths.vec3;
import com.gumse.maths.vec4;
import com.gumse.model.Model3D;
import com.gumse.shader.Shader;
import com.gumse.shader.ShaderProgram;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.textures.Texture;
import com.gumse.tools.FPS;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;

/**
 * Rendert eine Karte in 3D
 * 
 * @author Tom Beuke
 */
public class CardRenderer extends RenderGUI
{
    private static final float MAX_MOUSE_MOVE_ANGLE = 35.0f;

    private static Model3D pCardModel = null;
    private static ShaderProgram pCardShader = null;
    private static Camera pCamera = null;
    private static Framebuffer pFrontFramebuffer = null, pBackFramebuffer;
    private Card pCard;
    private final ivec2 viewportsize;
    private final ivec2 viewportpos;
    private vec3 v3TargetRotation;
    private boolean bCurrentSide, bDoneRotating;

    /**
     * Der Standardkonstruktor der Klasse CardRenderer
     */
    public CardRenderer()
    {
        this.vSize = new ivec2(100,100);
        this.viewportsize = new ivec2();
        this.viewportpos = new ivec2();
        this.v3TargetRotation = new vec3(0, 90, 0);
        this.bCurrentSide = false;
        this.bDoneRotating = true;

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
            pCardModel.setScale(new vec3(2.0f));
            pCardModel.setColor(new vec4(1.0f));

            pCamera = new Camera(90.0f);
            pCamera.setPosition(new vec3(0.0f, 0.0f, 10.0f));

            ivec2 resolution = new ivec2(980, 800);
            pFrontFramebuffer = new Framebuffer(resolution);
            pFrontFramebuffer.addTextureAttachment();
            pBackFramebuffer = new Framebuffer(resolution);
            pBackFramebuffer.addTextureAttachment();
            pCardModel.setTexture(pFrontFramebuffer.getTexture());
        }

        updateOnSizeChange();

        
        this.setSizeInPercent(true, true);
        reposition();
    }

    public void setCard(Card card)
    {
        this.pCard = card;
        this.v3TargetRotation = new vec3(0, 90, 0);
        this.bCurrentSide = false;
        this.bDoneRotating = true;

        updateOnSizeChange();
        pCardModel.setRotation(new vec3(0, 90, 0));
        renderToTexture();
    }

    /**
     * Dreht die dargestellte Karte um
     */
    public void flip()
    {
        if(bDoneRotating)
        {
            bDoneRotating = false;
            if(bCurrentSide = !bCurrentSide)
                v3TargetRotation.set(new vec3(0, -90, 0));
            else
                v3TargetRotation.set(new vec3(0, 90, 0));
        }
    }

    /**
     * Rendert eine Karte mittels zwei Framebuffer zu Texturen
     */
    public void renderToTexture()
    {
        pFrontFramebuffer.bind();
        CardTypesRenderer.renderFront(pCard, pFrontFramebuffer.getSize());
        pFrontFramebuffer.unbind();

        pBackFramebuffer.bind();
        CardTypesRenderer.renderBack(pCard, pBackFramebuffer.getSize());
        pBackFramebuffer.unbind();
    }

    @Override
    public void updateOnSizeChange()
    {
        float aspect = Framebuffer.CurrentlyBoundFramebuffer.getAspectRatio();
        aspect = 1.0f;
        float distance = 1.0f;
        pCamera.setProjectionMatrix(mat4.ortho(aspect * distance / 2, distance * 0.5f, -aspect * distance / 2, -distance * 0.5f, 0.1f, 1000.0f));

        viewportsize.set(vActualSize);
        viewportpos.set(vActualPos);

        if(viewportsize.y < viewportsize.x)
        {
            viewportsize.x = (int)(viewportsize.x * Framebuffer.CurrentlyBoundFramebuffer.getAspectRatio());
            viewportpos.x += (vActualSize.x - viewportsize.x) / 2;
        }
        else
        {
            viewportsize.y = (int)(viewportsize.y * Framebuffer.CurrentlyBoundFramebuffer.getAspectRatioWidthToHeight());
            viewportpos.y += (vActualSize.y - viewportsize.y) / 2;
        }
    }
    
    @Override
    public void renderextra()
    {
        renderchildren();

        GL30.glViewport(viewportpos.x, viewportpos.y, viewportsize.x, viewportsize.y);
        
        pCardShader.use();
        pCardShader.loadUniform("projectionMatrix", pCamera.getProjectionMatrix());
        pCardShader.loadUniform("viewMatrix", pCamera.getViewMatrix());
        pBackFramebuffer.getTexture().bind(1);
        
		GL30.glEnable(GL30.GL_DEPTH_TEST);
        pCardModel.render();
		GL30.glDisable(GL30.GL_DEPTH_TEST);
        pCardShader.unuse();

        Window.CurrentlyBoundWindow.resetViewport();
    }

    @Override
    public void updateextra()
    {
        if(!Mouse.isActiveHovering() && isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_ALL_SIDES_RESIZE);
            if(isHoldingLeftClick())
            {
                ivec2 delta = Window.CurrentlyBoundWindow.getMouse().getPositionDelta();
                v3TargetRotation.add(new vec3(delta.y, delta.x, 0));
                if(v3TargetRotation.x < -MAX_MOUSE_MOVE_ANGLE) v3TargetRotation.x = -MAX_MOUSE_MOVE_ANGLE;
                if(v3TargetRotation.x > MAX_MOUSE_MOVE_ANGLE)  v3TargetRotation.x = MAX_MOUSE_MOVE_ANGLE;

                if(bCurrentSide)
                {
                    if(v3TargetRotation.y < -90.0f - MAX_MOUSE_MOVE_ANGLE) v3TargetRotation.y = -90.0f - MAX_MOUSE_MOVE_ANGLE;
                    if(v3TargetRotation.y > -90.0f + MAX_MOUSE_MOVE_ANGLE) v3TargetRotation.y = -90.0f + MAX_MOUSE_MOVE_ANGLE;
                }
                else
                {
                    if(v3TargetRotation.y < 90.0f - MAX_MOUSE_MOVE_ANGLE) v3TargetRotation.y = 90.0f - MAX_MOUSE_MOVE_ANGLE;
                    if(v3TargetRotation.y > 90.0f + MAX_MOUSE_MOVE_ANGLE) v3TargetRotation.y = 90.0f + MAX_MOUSE_MOVE_ANGLE;
                }
                pCardModel.setRotation(v3TargetRotation);
            }
        }

        if(!pCardModel.getRotation().equalsRound(v3TargetRotation))
        {
            float speed = 10.0f * FPS.getFrametime();
            if(speed > 0.0f)
            {
                pCardModel.setRotation(
                    quat.toEuler(quat.rotateTowards(quat.toQuaternion(pCardModel.getRotation()), 
                                quat.toQuaternion(v3TargetRotation), 
                                speed))
                );
            }
        }
        else
        {
            bDoneRotating = true;
        }
    }

    public Texture getFrontTexture()
    {
        return pFrontFramebuffer.getTexture();
    }

    public Texture getBackTexture()
    {
        return pBackFramebuffer.getTexture();
    }
}