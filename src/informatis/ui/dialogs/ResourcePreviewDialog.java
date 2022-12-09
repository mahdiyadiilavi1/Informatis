package informatis.ui.dialogs;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import java.lang.reflect.Field;


public class ResourcePreviewDialog extends BaseDialog {
    boolean showName = false;
    int currentTab = 0;

    public ResourcePreviewDialog() {
        super("resource previews");
        setFillParent(true);
        addCloseButton();

        cont.table(t -> {
            t.top().center();
            t.table(Tex.underline, tabTable -> {
                String[] tabs = {"Textures", "Styles", "Colors"};
                for(int i = 0; i < tabs.length; i++) {
                    int j = i;
                    TextButton button = new TextButton(tabs[j], Styles.flatToggleMenut);
                    button.clicked(() -> {
                        currentTab = j;
                        button.toggle();
                        for(int elemI = 0; elemI < tabTable.getChildren().size; elemI++) {
                            ((Button) tabTable.getChildren().get(elemI)).setChecked(currentTab == elemI);
                        }
                        refreshResourceTable();
                    });
                    tabTable.add(button).height(50).growX();
                }
            }).growX().row();
            t.table(tt -> tt.add(rebuildResourceList())).name("resource").grow();
        }).grow();
    }
    
    void refreshResourceTable() {
        Table resource = find("resource");
        resource.clearChildren();
        resource.add(rebuildResourceList()).grow();
    }

    float scrollY, scrollY2;
    String search = "";
    String colorInput1 = "ffffffff", colorInput2 = "ffffffff";
    float colorMixProg = 0;
    int colorMixSelectIndex = 0;
    Color color1 = Color.white, color2 = Color.white, mixedColor = Color.white;
    Table rebuildResourceList() {
        return new Table(pane -> {
            Cons[] builders = {
                (Cons<Table>) ppane -> {
                    ppane.table(options -> {
                        options.top().center().defaults().pad(20);
                        options.table(t -> {
                            t.button(Icon.zoom, this::refreshResourceTable);
                            t.field(search, value -> search = value).get().keyDown(KeyCode.enter, this::refreshResourceTable);
                        }).minWidth(200);
                        options.check("show resource with its name", showName, (checkBox) -> {
                            showName = !showName;
                            refreshResourceTable();
                        });
                    }).padBottom(20f).growX().row();
                    ScrollPane contentPane = new ScrollPane(new Table(content -> {
                        buildTitle(content, "Texture Resources").row();
                        content.table(this::buildTexResources).grow().row();
                        buildTitle(content, "Icon Resources").row();
                        content.table(this::buildIconResources).grow().row();
                    }));
                    contentPane.scrolled(y -> scrollY = contentPane.getScrollY());
                    contentPane.layout();
                    contentPane.setScrollY(scrollY);
                    ppane.add(contentPane).grow();
                },
                (Cons<Table>) ppane -> {
                    buildTitle(ppane, "Styles Resources").row();
                    ppane.pane(this::buildStyleResources).grow().fill();
                },
                (Cons<Table>) ppane -> {
                    ppane.table(options -> {
                        options.top().center().defaults().pad(20);

                        options.add("Mix");
                        options.add(new Image() {
                            @Override
                            public void draw() {
                                super.draw();

                                int size = 8;
                                Draw.color(colorMixSelectIndex == 0 ? Pal.accent : Pal.gray);
                                Draw.alpha(parentAlpha);
                                Lines.stroke(Scl.scl(3f));
                                Lines.rect(x - size / 2f, y - size / 2f, width + size, height + size);
                                Draw.reset();
                            }
                        }).size(30).color(color1).pad(10).get().clicked(() -> colorMixSelectIndex = 0);
                        options.field(colorInput1, field -> {
                            colorInput1 = field;
                            color1 = Color.valueOf(field.matches("^#?[a-fA-F0-9]{6,8}$") ? field : "ffffff");
                        }).get().keyDown(KeyCode.enter, this::refreshResourceTable);
                        options.slider(0, 100, 1, colorMixProg, prog -> {
                            colorMixProg = prog;
                            mixedColor = color1.cpy().lerp(color2, prog / 100);
                        }).get().addListener(new ClickListener(){
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                                refreshResourceTable();
                                super.touchUp(event, x, y, pointer, button);
                            }
                        });
                        options.add(new Image() {
                            @Override
                            public void draw() {
                                super.draw();

                                int size = 8;
                                Draw.color(colorMixSelectIndex == 1 ? Pal.accent : Pal.gray);
                                Draw.alpha(parentAlpha);
                                Lines.stroke(Scl.scl(3f));
                                Lines.rect(x - size / 2f, y - size / 2f, width + size, height + size);
                                Draw.reset();
                            }
                        }).size(30).color(color2).pad(10).get().clicked(() -> colorMixSelectIndex = 1);
                        options.field(colorInput2, field -> {
                            colorInput2 = field;
                            color2 = Color.valueOf(field.matches("^#?[a-fA-F0-9]{6,8}$") ? field : "ffffff");
                        }).get().keyDown(KeyCode.enter, this::refreshResourceTable);
                        options.row();
                        options.add("---->");
                        options.add(new Image(){
                            @Override
                            public void draw() {
                                this.setColor(mixedColor);
                                super.draw();
                            }
                        }).size(30).pad(10);
                        options.label(() -> mixedColor.toString());
                        options.button(Icon.refresh, this::refreshResourceTable);

                    }).growX().row();
                    buildTitle(ppane, "Color Resources").row();
                    ScrollPane contentPane = new ScrollPane(new Table(this::buildColorResources));
                    contentPane.scrolled(y -> scrollY2 = contentPane.getScrollY());
                    contentPane.layout();
                    contentPane.setScrollY(scrollY2);
                    ppane.add(contentPane).grow().fill();
                }
            };
            pane.table(ppane -> {
                ppane.left().top();
                builders[currentTab].get(ppane);
            }).grow();
        });
    }

    Table buildTitle(Table table, String title) {
        table.table(Tex.underline2, tex -> tex.add(title)).pad(30f, 0f, 30f, 0f).left();

        return table;
    }

    void applyColors(Table table, Class<?> target) {
        buildTitle(table, target.getName()).row();
        table.table(t -> {
            t.top().left();
            Field[] palFields = target.getDeclaredFields();
            int row = 0;
            for(Field palField : palFields) {
                if(!palField.getType().equals(Color.class)) continue;

                try {
                    Object obj = palField.get(null);
                    if(!(obj instanceof Color color)) continue;

                    t.table(colorCell -> {
                        colorCell.left();
                        colorCell.image().size(30).color(color).tooltip("#" + color.toString());
                        colorCell.add(palField.getName()).padLeft(10);
                    }).maxWidth(300).growX().pad(20).get().clicked(() -> {
                        if(colorMixSelectIndex == 0) {
                            colorInput1 = color.toString();
                            color1 = color;
                        } else {
                            colorInput2 = color.toString();
                            color2 = color;
                        }
                        mixedColor = color1.cpy().lerp(color2, colorMixProg / 100);
                        refreshResourceTable();
                    });
                    if(++row % 8 == 0) t.row();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }).grow().padLeft(50);
    }
    Table buildColorResources(Table table) {
        table.top().left();

        applyColors(table, Pal.class);
        table.row();
        applyColors(table, Color.class);
        return table;
    }

    Table buildStyleResources(Table table) {
        Cons<Class<?>> build = classz -> {
            Seq<Field> allStyles = Seq.with(Styles.class.getFields()).filter(field -> field.getType().equals(classz));

            String[] spliten = classz.getName().split("\\$");
            buildTitle(table, spliten[spliten.length - 1]).marginLeft(20f).row();

            table.table(t -> {
                t.top().left().defaults().labelAlign(Align.center).center().maxHeight(50).pad(10).grow();

                if(classz.equals(Drawable.class)) {
                    for(Field field : allStyles) {
                        t.table(tt -> {
                            tt.left();
                            try {
                                tt.add(field.getName());
                                tt.image((Drawable) field.get(null)).grow().padLeft(40f);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }).row();
                    }
                    return;
                }

                Seq<Field> styles = Seq.with(classz.getFields()).filter(style -> allStyles.find(field -> {
                    style.setAccessible(true);
                    try {
                        Object value = style.get(field.get(null));
                        return !(value == null || value instanceof Font || value.toString().matches("^\\d*.\\d*$")); //wtf
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }) != null);

                t.add("");
                styles.each(style -> t.table(tt -> {
                    tt.center();
                    tt.add(style.getName());
                }));
                t.row();
                t.image().height(4f).color(Pal.accent).growX().colspan(styles.size + 1).row();
                for(Field field : allStyles) {
                    try {
                        t.add(field.getName());
                        Object style = field.get(null);
                        styles.each(styleField -> {
                            styleField.setAccessible(true);
                            try {
                                Object value = styleField.get(style);
                                t.table(tt -> {
                                    tt.center();
                                    if(value == null) tt.add("").color(Color.gray);
                                    else if(value instanceof Drawable drawable) tt.image(drawable).grow();
                                    else tt.add(value.toString())
                                            .color(value.toString().matches("^#?[a-fA-F0-9]{6,8}$")
                                                    ? Color.valueOf(value.toString())
                                                    : Color.gray
                                            );
                                });
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        t.row();
                        //divider smh?
                        t.image().height(4f).color(Pal.gray).growX().colspan(styles.size + 1).row();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).grow().row();
        };

        build.get(Drawable.class);
        build.get(Button.ButtonStyle.class);
        build.get(TextButton.TextButtonStyle.class);
        build.get(ImageButton.ImageButtonStyle.class);
        return table;
    }

    Table buildIconResources(Table table) {
        int r = 0;
        for(ObjectMap.Entry<String, TextureRegionDrawable> entry : Icon.icons.entries()) {
            if(!entry.key.contains(search)) continue;
            addResourceImage(table, entry.value, entry.key, 0);
            if(++r % 15 == 0) table.row();
        }
        return table;
    }

    Table buildTexResources(Table table) {
        Field[] fields = Tex.class.getDeclaredFields();
        int r = 0;
        for(int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                if(!field.getName().contains(search)) continue;
                addResourceImage(table, (Drawable) field.get(null), field.getName(), i);
                if(++r % 15 == 0) table.row();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return table;
    }

    void addResourceImage(Table table, Drawable res, String name, int i) {
        table.table(t -> {
            t.center().defaults();

            Image image = new Image(res).setScaling(Scaling.bounded);
            Label label = new Label(name);
            label.setWidth(100);
            label.setFontScale(0.75f);
            label.setAlignment(Align.center);
            if(showName) {
                t.stack(
                    new Table(tt -> {
                        tt.center();
                        tt.add(image).size(100);
                    }),
                    new Table(tt -> {
                        tt.center();
                        tt.addChild(label);
                        label.setPosition(t.x + t.getWidth() / 2, label.y + (name.length() >= 13 && i % 2 == 0 ? -label.getHeight() * 0.9f : 0));
                        tt.pack();
                    })
                ).center().maxSize(100);
            }
            else t.add(image).size(100);
        }).size(100).pad(10).tooltip(name);
    }
}
