package com.swp.GUI.Cards.CardRenderer;

import com.gumse.basics.Camera;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.mat4;
import com.gumse.maths.vec3;
import com.gumse.maths.vec4;
import com.gumse.model.Model3D;
import com.gumse.system.Window;
import com.gumse.textures.Texture;
import com.gumse.tools.FPS;
import com.swp.DataModel.Card;

public class CardRenderer extends RenderGUI
{
    private Model3D pCardModel;
    private Camera pCamera;

    public CardRenderer(Card card)
    {
        this.vSize = new ivec2(100,100);

        //pRatingGUI = new RatingGUI(card);
        addGUI(XMLGUI.loadFile("guis/cardrendererlayout.xml"));

        pCardModel = new Model3D(null);
        pCardModel.load("models/card.obj");
        pCardModel.setPosition(new vec3(0,1,0));
        pCardModel.setScale(new vec3(1.5f));
        pCardModel.setColor(new vec4(0.26f, 0.26f, 0.31f, 1.0f));

        pCamera = new Camera(90.0f);
        pCamera.setPosition(new vec3(0, 0, 10.0f));
        updateOnSizeChange();
        //pCamera.setProjectionMatrix(Window.CurrentlyBoundWindow.getScreenMatrix());


        
        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public Texture renderToTexture()
    {
        Texture retTex = new Texture();
        
        return retTex;
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
        
        Model3D.getDefaultShader().use();
        Model3D.getDefaultShader().loadUniform("projectionMatrix", pCamera.getProjectionMatrix());
        Model3D.getDefaultShader().loadUniform("viewMatrix", pCamera.getViewMatrix());
        pCardModel.increaseRotation(new vec3(0, FPS.getFrametime() * 100.0f, 0));
        pCardModel.render();
        Model3D.getDefaultShader().unuse();

        renderchildren();
    }
}