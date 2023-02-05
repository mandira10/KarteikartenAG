package com.swp.GUI.Category;

import java.util.List;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.GumMath;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec2;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Category;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;

/**
 * Diese Seite wird dazu verwendet alle Kategorien in einem Baumdiagramm darzustellen
 */
public class ViewCategoryTreePage extends Page
{
    private RenderGUI pCanvas;
    private TreeNode pRootNode;
    private ivec2 v2MouseOffset;
    private boolean bActiveMoving;
    private static final String ROOT_NODE = "Root";
    private static final vec2 GAP_SIZE = new vec2(20, 50);
    private static final float CONNECTOR_WIDTH = 5;
    private static final float TEXT_SIZE = 40;
    private static final float NODE_HEIGHT = 60;
    private float fZoomLevel = 1.0f;

    private class TreeNode extends TextBox
    {
        Box connectorBox = null;

        TreeNode(String name, boolean isroot)
        {
            super(name, FontManager.getInstance().getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
            setType("TreeNode");
            setTextSize((int)(TEXT_SIZE * fZoomLevel));
            setSize(new ivec2(getTextSize().x + 20, (int)(NODE_HEIGHT * fZoomLevel)));

            if(!isroot)
            {
                connectorBox = new Box(new ivec2(0, 0), new ivec2((int)(CONNECTOR_WIDTH * fZoomLevel)));
                addElement(connectorBox);
            }

            if(name.equals(ROOT_NODE))
            {
                getBox().hide(true);
                getText().hide(true);
            }
        }

        /**
         *         ___ 
         *          x
         *     _________
         *    |_________|
         *         \pos1      |
         *          \         | y
         *        ___\pos2_   |
         *       |_________|
         */
        public void repositionConnectionToParent()
        {
            if(connectorBox != null)
            {
                ivec2 pos1 = ivec2.add(pParent.getPosition(), new ivec2(pParent.getSize().x / 2, pParent.getSize().y - (int)(CONNECTOR_WIDTH * fZoomLevel) / 2));
                ivec2 pos2 = ivec2.add(vActualPos, new ivec2(vActualSize.x / 2, (int)(CONNECTOR_WIDTH * fZoomLevel) / 2));
                ivec2 diff = ivec2.sub(pos2, pos1);

                float angle = (float)Math.toDegrees(Math.atan((double)diff.x / (double)diff.y)) + 90;
                int length = (int)ivec2.length(diff) + (int)(CONNECTOR_WIDTH * fZoomLevel);

                connectorBox.setRotation(angle);
                connectorBox.setPosition(new ivec2(50, (int)(CONNECTOR_WIDTH * fZoomLevel) / 2));
                connectorBox.setPositionInPercent(true, false);
                connectorBox.setSize(new ivec2(length, (int)(CONNECTOR_WIDTH * fZoomLevel)));
                connectorBox.setOrigin(new ivec2(0, (int)(CONNECTOR_WIDTH * fZoomLevel)));
                connectorBox.setRotationOrigin(new ivec2((int)(CONNECTOR_WIDTH * fZoomLevel) / 2));       
            }
        }

        public void repositionNode(int siblingoffset)
        {
            int extraoffset = 0;
            if(!sTitle.equals(ROOT_NODE) && numChildren() > 1)
                extraoffset = getWidth() / 2;
            
            setPosition(new ivec2(siblingoffset + extraoffset, vActualSize.y + (int)(GAP_SIZE.y * fZoomLevel)));
            repositionConnectionToParent();
            

            int nextoffset = 0;
            for(int i = 0; i < numChildren(); i++)
            {
                TreeNode entry = (TreeNode)getChild(i);
                entry.repositionNode(nextoffset - extraoffset);
                nextoffset += entry.getWidth();
            }
        }

        public int getWidth()
        {
            int width = 0;
            for(RenderGUI child : vChildren)
                width += ((TreeNode)child).getWidth();
            if(width == 0)
                width = vActualSize.x;

            width += GAP_SIZE.x;
            //Output.info(getTitle() + ": " + width);
            return width;
        }

        public void zoomUpdate()
        {
            setTextSize((int)(TEXT_SIZE * fZoomLevel));
            setSize(new ivec2(getTextSize().x + 20, (int)(NODE_HEIGHT * fZoomLevel)));

            for(RenderGUI child : vChildren)
                ((TreeNode)child).zoomUpdate();
        }
    }

    /**
     * Der Standardkonstruktor der Klasse ViewCategoryTreePage
     */
    public ViewCategoryTreePage()
    {
        super("Category Tree", "categorytreepage");
        this.vSize = new ivec2(100,100);
        this.bActiveMoving = false;

        addGUI(XMLGUI.loadFile("guis/categories/categorytreepage.xml"));

        pCanvas = findChildByID("canvas");
        pCanvas.onHover(null, Mouse.GUM_CURSOR_ALL_SIDES_RESIZE);

        RenderGUI optionsMenu = findChildByID("menu");
        Button treeButton = (Button)optionsMenu.findChildByID("listviewbutton");
        treeButton.onClick((RenderGUI gui) -> {
            ((CategoryOverviewPage)PageManager.viewPage(PAGES.CATEGORY_OVERVIEW)).loadCategories();
        });
        
        Button newButton = (Button)optionsMenu.findChildByID("addcategorybutton");
        newButton.onClick((RenderGUI gui) -> {
            ((EditCategoryPage)PageManager.viewPage(PAGES.CATEGORY_EDIT)).editCategory(new Category(),true);
        });

        this.setSizeInPercent(true, true);
        reposition();
    }

    @Override
    public void updateextra() 
    {
        if(isMouseInside())
        {
            if(isHoldingLeftClick())
            {
                if(!Mouse.isBusy())
                {
                    Mouse.setBusiness(true);
                    bActiveMoving = true;
                    v2MouseOffset = ivec2.sub(ivec2.sub(pRootNode.getPosition(), Window.CurrentlyBoundWindow.getMouse().getPosition()), pCanvas.getPosition());
                }
            }
            else
            {
                Mouse.setBusiness(false);
                bActiveMoving = false;
            }

            int scrollPos = Window.CurrentlyBoundWindow.getMouse().getMouseWheelState();
            if(scrollPos != 0)
            {
                float diff = (float)scrollPos * 0.1f;
                fZoomLevel += diff;
                fZoomLevel = GumMath.clamp(fZoomLevel, 0.0f, 10.0f);
                float percentage = (float)pRootNode.getRelativePosition().x / (float)pRootNode.getWidth();
                int oldy = pRootNode.getRelativePosition().y;

                pRootNode.zoomUpdate();
                pRootNode.repositionNode(0);
                pRootNode.setPosition(new ivec2((int)((float)pRootNode.getWidth() * percentage), oldy));
            }
        }


        if(bActiveMoving)
        {
            Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
            pRootNode.setPosition(ivec2.add(mouse.getPosition(), v2MouseOffset));
        }
    }

    /**
     * Setzt das Diagramm zurück
     */
    public void reset()
    {
        pCanvas.destroyChildren();

        pRootNode = new TreeNode(ROOT_NODE, true);
        pCanvas.addGUI(pRootNode);

        CategoryController.getInstance().getRootCategories(false, new DataCallback<Category>() {
            @Override public void onFailure(String msg) {}
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                addCategories(pRootNode, categories);
            }
        });

        pRootNode.repositionNode(0);

        resize();
        reposition();
    }

    private void addCategories(RenderGUI parent, List<Category> categories)
    {
        for(int i = 0; i < categories.size(); i++)
        {
            final Category category = categories.get(i);
            TreeNode node = new TreeNode(categories.get(i).getName(), parent == pRootNode);
            CategoryController.getInstance().getChildrenForCategory(category, new DataCallback<Category>() {
                @Override public void onFailure(String msg) { 
                    NotificationGUI.addNotification(msg, NotificationType.ERROR, 5);
                }
                @Override public void onInfo(String msg) {}
                @Override public void onSuccess(List<Category> categories)
                {
                    addCategories(node, categories);
                }
            });
            node.onHover(null, Mouse.GUM_CURSOR_HAND);
            node.onClick((RenderGUI thisnode) -> {
                ((ViewSingleCategoryPage)PageManager.viewPage(PAGES.CATEGORY_SINGLEVIEW)).setCategory(category);
            });
            parent.addGUI(node);
        }
        pRootNode.repositionNode(0);
    }
}
