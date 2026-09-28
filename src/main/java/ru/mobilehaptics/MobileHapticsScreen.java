package ru.mobilehaptics;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class MobileHapticsScreen extends Screen {
    private final Screen parent;

    public MobileHapticsScreen(Screen parent) {
        super(Text.translatable("screen.mobile-haptics.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = this.height / 2 - 80;

        addDrawableChild(ButtonWidget.builder(enabledText(), b -> {
            MobileHapticsClient.CONFIG.enabled = !MobileHapticsClient.CONFIG.enabled;
            MobileHapticsClient.CONFIG.save();
            b.setMessage(enabledText());
        }).dimensions(center - 100, y, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(breakText(), b -> {
            MobileHapticsClient.CONFIG.breakMs = cycle(MobileHapticsClient.CONFIG.breakMs, 10, 25, 40, 75, 120);
            b.setMessage(breakText());
            MobileHapticsClient.CONFIG.save();
        }).dimensions(center - 100, y + 25, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(placeText(), b -> {
            MobileHapticsClient.CONFIG.placeMs = cycle(MobileHapticsClient.CONFIG.placeMs, 5, 10, 15, 30, 60);
            b.setMessage(placeText());
            MobileHapticsClient.CONFIG.save();
        }).dimensions(center - 100, y + 50, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.mobile-haptics.test"), b ->
                NativeVibrator.vibrate(50, 80)
        ).dimensions(center - 100, y + 75, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.mobile-haptics.done"), b ->
                this.client.setScreen(parent)
        ).dimensions(center - 100, y + 110, 200, 20).build());
    }

    private Text enabledText() {
        return Text.translatable("screen.mobile-haptics.enabled", MobileHapticsClient.CONFIG.enabled ? "ON" : "OFF");
    }

    private Text breakText() {
        return Text.translatable("screen.mobile-haptics.break", MobileHapticsClient.CONFIG.breakMs, percent(MobileHapticsClient.CONFIG.breakAmplitude));
    }

    private Text placeText() {
        return Text.translatable("screen.mobile-haptics.place", MobileHapticsClient.CONFIG.placeMs, percent(MobileHapticsClient.CONFIG.placeAmplitude));
    }

    private static int percent(int amplitude) {
        return Math.round(amplitude * 100f / 255f);
    }

    private static int cycle(int current, int... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) return values[(i + 1) % values.length];
        }
        return values[0];
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}