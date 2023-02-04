package com.swp.GUI.Extras;

import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.tools.Output;
import com.gumse.tools.FPS;

/**
 * Eine Benachrichtigung für
 * die NotificationGUI Klasse
 */
public class Notification extends RenderGUI
{
    /**
     * Die art von Benachrichtigung
     */
    public enum NotificationType
    {
        /** Informationen */       INFO,
        /** Warnungen */           WARNING,
        /** Fehler */              ERROR,
        /** Kritische Fehler */    CRITICAL,
        /** Debug-Info */          DEBUG,
        /** Datenbankverbindung */ CONNECTION
    };

    private Box pBackground;
    private Box pProgressBar;
    private float fProgress;
    private float fSpeed;
    private boolean bCanBeRemoved;

    /**
     * Der Hauptkonstruktor der Klasse Notification
     * @param str   Der Nachrichtentext
     * @param pos   Position des GUIs in Pixeln
     * @param type  Art von Benachrichtigung
     * @param speed Geschwindigkeit in welcher die Nachricht wieder verschwindet
     */
    public Notification(String str, ivec2 pos, NotificationType type, float speed)
    {
        ivec2 boxSize = new ivec2(300, 30);
        this.vSize.set(boxSize);
        this.vPos.set(pos);
        this.fProgress = 1.0f;
        this.fSpeed = speed;
        this.bCanBeRemoved = false;

        pBackground = new Box(new ivec2(0, 0), boxSize);
        pBackground.setColor(new vec4(0.45f, 0.46f, 0.46f, 1));
        //pBackground.setCornerRadius(new vec4(5));
        addElement(pBackground);

        pProgressBar = new Box(new ivec2(0, 0), new ivec2(100, 1));
        pProgressBar.setSizeInPercent(true, false);
        pProgressBar.setColor(new vec4(0.94f, 0.58f, 0.17f, 1));
        pBackground.addGUI(pProgressBar);

        TextBox notificationTextbox = new TextBox(str, FontManager.getInstance().getDefaultFont(), new ivec2(100, 0), new ivec2(100, 100));
        notificationTextbox.setOrigin(new ivec2(100, 0));
        notificationTextbox.setOriginInPercent(true, false);
        notificationTextbox.setSizeInPercent(true, true);
        notificationTextbox.setPositionInPercent(true, false);
        notificationTextbox.setMargin(new ivec2(-boxSize.y - 15, 0));
        notificationTextbox.setTextSize(20);
        notificationTextbox.setAlignment(Alignment.LEFT);
        notificationTextbox.getBox().hide(true);
        notificationTextbox.setAutoInsertLinebreaks(true);
        pBackground.addGUI(notificationTextbox);

        Text notificationIcon = new Text("", FontManager.getInstance().getFont("FontAwesome"), new ivec2(5, 5), 0);
        notificationIcon.setCharacterHeight(boxSize.y);
        pBackground.addGUI(notificationIcon);

        setSize(new ivec2(vSize.x, notificationTextbox.getText().getSize().y + 20));
        pBackground.setSize(vActualSize);

        vec4 color = new vec4(0.0f);

        switch(type)
        {
            case CRITICAL:
                notificationIcon.setString("");
                color = new vec4(0.7f, 0.22f, 0.22f, 1);
                break;
                
            case DEBUG:
                notificationIcon.setString("");
                color = new vec4(0.94f, 0.58f, 0.17f, 1);
                break;

            case ERROR:
                notificationIcon.setString("");
                color = new vec4(0.91f, 0.3f, 0.24f, 1);
                break;

            case INFO:
                notificationIcon.setString("");
                color = new vec4(0.65f, 0.78f, 0.9f, 1);
                break;

            case WARNING:
                notificationIcon.setString("");
                color = new vec4(0.98f, 0.79f, 0.14f, 1);
                break;

            case CONNECTION:
                notificationIcon.setString("");
                notificationIcon.setPosition(new ivec2(5, 7));
                notificationIcon.setCharacterHeight(boxSize.y - 5);
                color = new vec4(0.18f, 0.8f, 0.44f, 1);
                break;
            default: Output.error("Unknown Notification type"); break;

        };

        notificationIcon.setColor(color);
        notificationTextbox.setTextColor(color);

        onHover(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                overrideAlpha(1.0f);
                fProgress = 1.0f;
    
                if(!RenderGUI.somethingHasBeenClicked() && isClicked())
                {
                    bCanBeRemoved = true;
                }
            }
        }, iHoverCursorShape);

        resize();
        reposition();
    }

    @Override
    public void updateextra() 
    {
        fProgress -= FPS.getFrametime() * fSpeed;

        if(fProgress <= 0)
        {
            overrideAlpha(fAlphaOverride - FPS.getFrametime() * 6);
            if(fAlphaOverride <= 0)
                bCanBeRemoved = true;
        }

        pProgressBar.setSize(new ivec2((int)(100 * fProgress), 1));
        pProgressBar.setColor(vec4.mix(new vec4(1, 0, 0, 1), new vec4(0, 1, 0, 1), fProgress));
    }

    boolean canBeRemoved() { return bCanBeRemoved; }
}