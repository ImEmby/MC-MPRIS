package de.emby.mcpris;

import de.emby.mcpris.utils.Color;
import de.emby.mcpris.config.Config;
import de.emby.mcpris.utils.Song;
import de.emby.mcpris.utils.Text;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import static java.lang.Math.max;

@EventBusSubscriber(
        modid = Mcpris.MODID,
        value = Dist.CLIENT
)
public class McprisHud {

    public static boolean visible = true;

    private static void draw(net.minecraft.client.gui.GuiGraphics g) {
        if (!visible) return;

        Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int moduleWidth = Config.width.get();
        int moduleHeight = Config.height.get();

        int margin = Config.margin.get();
        int position = Config.position.get();

        int line_width = Config.line_width.get();
        int line_position = Config.line_position.get();

        int bg_color = Color.hexToArgb(Config.bg_color.get(), "#FF0000FF");
        int line_color = Color.hexToArgb(Config.line_color.get(), "#FF0000FF");
        int text_color = Color.hexToArgb(Config.text_color.get(), "#FF0000FF");
        int subtext_color = Color.hexToArgb(Config.subtext_color.get(), "#FF0000FF");

        float title_size = max(0.01f, (float) Config.title_size.get() / 100);
        float artist_size = max(0.01f, (float) Config.artist_size.get() / 100);
        int line_spacing = Config.line_spacing.get();

        String title = Song.getTitle();
        String artist = Song.getArtist();
        String elide = Config.elide.get();
        int title_width = (int) (Text.getTextWidth(title) * title_size);
        int artist_width = (int) (Text.getTextWidth(artist) * artist_size);
        int title_height = (int) (Text.getTextHeight() * title_size);
        int artist_height = (int) (Text.getTextHeight() * artist_size);

        boolean use_dynamic_width = Config.use_dynamic_width.get();
        boolean use_dynamic_height = Config.use_dynamic_height.get();
        boolean use_dynamic_line_spacing = Config.use_dynamic_line_spacing.get();

        if (!use_dynamic_width) {
            title = Text.truncate(title, (int) ((moduleWidth - 20) / title_size), elide);
            artist = Text.truncate(artist, (int) ((moduleWidth - 20) / artist_size), elide);
        } else {
            moduleWidth = max(title_width, artist_width) + 20;
        }

        if (use_dynamic_line_spacing) {
            line_spacing = (int) (Text.getTextHeight() * (title_size + artist_size) / 2 + 5);
        }

        if (use_dynamic_height) {
            moduleHeight = max(8 + title_height, 8 + line_spacing + artist_height) + 8;
        }

        int x = margin;
        int y = margin;
        int line_x;

        switch (position) {
            case 1:
                x = screenWidth - moduleWidth - margin;
                break;

            case 2:
                y = screenHeight - moduleHeight - margin;
                break;

            case 3:
                x = screenWidth - moduleWidth - margin;
                y = screenHeight - moduleHeight - margin;
                break;
        }

        if (line_position == 0) {
            line_x = x;
        } else {
            line_x = x + moduleWidth - line_width;
        }

        x = Math.clamp(x, 0, max(0, screenWidth - moduleWidth));
        y = Math.clamp(y, 0, max(0, screenHeight - moduleHeight));

        g.fill(x, y, x + moduleWidth, y + moduleHeight, bg_color);
        g.fill(line_x, y, line_x + line_width, y + moduleHeight, line_color);

        g.pose().pushPose();
        g.pose().scale(title_size, title_size, 1.0f);
        g.drawString(
                mc.font,
                title,
                (int) ((x + 10) / title_size),
                (int) ((y + 8) / title_size),
                text_color
        );
        g.pose().popPose();

        g.pose().pushPose();
        g.pose().scale(artist_size, artist_size, 1.0f);
        g.drawString(
                mc.font,
                artist,
                (int) ((x + 10) / artist_size),
                (int) ((y + 8 + line_spacing) / artist_size),
                subtext_color
        );
        g.pose().popPose();
    }

    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            draw(event.getGuiGraphics());
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        draw(event.getGuiGraphics());
    }
}
