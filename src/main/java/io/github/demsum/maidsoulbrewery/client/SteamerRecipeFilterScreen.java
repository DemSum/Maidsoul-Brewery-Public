package io.github.demsum.maidsoulbrewery.client;

import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.TouhouImageButton;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.TouhouStateSwitchButton;
import com.github.wallev.maidsoulkitchen.client.gui.entity.maid.MaidTaskConfigGui;
import com.github.wallev.maidsoulkitchen.client.gui.widget.button.TaskInfoButton;
import com.github.wallev.maidsoulkitchen.client.gui.widget.button.TypeButton;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import com.github.wallev.maidsoulkitchen.inventory.tooltip.AmountTooltip;
import io.github.demsum.maidsoulbrewery.menu.SteamerRecipeFilterMenu;
import io.github.demsum.maidsoulbrewery.network.SetSteamerFilterPayload;
import io.github.demsum.maidsoulbrewery.recipe.RecipeFilterData;
import io.github.demsum.maidsoulbrewery.recipe.RecipeOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SteamerRecipeFilterScreen extends MaidTaskConfigGui<SteamerRecipeFilterMenu> {
    private static final ResourceLocation COOK_GUIDE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("maidsoulkitchen", "textures/gui/cook_guide.png");
    private static final int GRID_COLUMNS = 7;
    private static final int GRID_ROWS = 4;
    private static final int RECIPES_PER_PAGE = GRID_COLUMNS * GRID_ROWS;
    private static final int RECIPE_BUTTON_SIZE = 20;
    private static final int RECIPE_BUTTON_SPACING = 2;

    private final List<RecipeButton> recipeButtons = new ArrayList<>();
    private List<RecipeOption> recipes = List.of();
    private RecipeFilterData filterData = RecipeFilterData.DEFAULT;
    private Button previousPageButton;
    private Button nextPageButton;
    private int page;

    public SteamerRecipeFilterScreen(
            SteamerRecipeFilterMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, Component.translatable("gui.maidsoulkitchen.cook_setting_screen.title"));
    }

    @Override
    protected void initAdditionData() {
        super.initAdditionData();
        recipes = menu.getRecipeOptions();
        filterData = menu.getFilterData();
        recipeButtons.clear();
        page = 0;
    }

    @Override
    protected void initAdditionWidgets() {
        super.initAdditionWidgets();
        int visualLeft = visualZone.startX();
        int visualTop = visualZone.startY();

        addRenderableWidget(new TaskInfoButton(visualLeft + 6, visualTop + 20, 70, 20, task));
        addModeButton(visualLeft, visualTop);
        addScrollButtons(visualLeft, visualTop);
        rebuildRecipeButtons();
    }

    @Override
    protected void renderAddition(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int separatorX = visualZone.startX() + 159;
        graphics.fill(separatorX, visualZone.startY() + 22, separatorX + 1, visualZone.startY() + 40, 0xFF000000);
        drawScrollBar(graphics);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        for (RecipeButton button : recipeButtons) {
            if (button.isHovered()) {
                ItemStack result = button.recipe().result();
                List<Component> text = Screen.getTooltipFromItem(minecraft, result);
                Optional<TooltipComponent> details = Optional.of(createAmountTooltip(button.recipe()));
                graphics.renderTooltip(font, text, details, result, mouseX, mouseY);
                return;
            }
        }
    }

    private AmountTooltip createAmountTooltip(RecipeOption recipe) {
        String mode = filterData.mode() == RecipeFilterData.Mode.WHITELIST
                ? CookData.Mode.WHITELIST.name
                : CookData.Mode.BLACKLIST.name;
        CookData cookData = new CookData(
                mode,
                filterData.whitelist().stream().map(ResourceLocation::toString).toList(),
                filterData.blacklist().stream().map(ResourceLocation::toString).toList()
        );
        return new AmountTooltip(
                recipe.id().toString(),
                recipe.ingredients(),
                filterData.mode() == RecipeFilterData.Mode.BLACKLIST,
                false,
                cookData
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        boolean inside = mouseX >= visualZone.startX()
                && mouseY >= visualZone.startY()
                && mouseX < visualZone.startX() + visualZone.width()
                && mouseY < visualZone.startY() + visualZone.height();
        if (inside && deltaY != 0.0) {
            changePage(deltaY > 0.0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void addModeButton(int visualLeft, int visualTop) {
        boolean whitelist = filterData.mode() == RecipeFilterData.Mode.WHITELIST;
        addRenderableWidget(new TypeButton(visualLeft + 160, visualTop + 22, 18, 18, whitelist) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                toggleMode();
                toggleState();
            }
        });
    }

    private void addScrollButtons(int visualLeft, int visualTop) {
        int x = visualLeft + 161;
        int y = visualTop + 44;
        previousPageButton = addRenderableWidget(new TouhouImageButton(
                x, y, 9, 7, 199, 74, 14, COOK_GUIDE_TEXTURE, button -> changePage(-1)
        ));
        nextPageButton = addRenderableWidget(new TouhouImageButton(
                x, y + 79, 9, 7, 208, 74, 14, COOK_GUIDE_TEXTURE, button -> changePage(1)
        ));
    }

    private void toggleMode() {
        filterData = filterData.withMode(filterData.mode().next());
        syncFilter();
        rebuildRecipeButtons();
    }

    private void toggleRecipe(RecipeOption recipe) {
        filterData = filterData.toggle(recipe.id());
        syncFilter();
        rebuildRecipeButtons();
    }

    private void syncFilter() {
        int maidId = menu.getMaidEntityId();
        if (maidId >= 0) {
            PacketDistributor.sendToServer(new SetSteamerFilterPayload(maidId, filterData));
        }
    }

    private void changePage(int change) {
        int changed = Mth.clamp(page + change, 0, maxPage());
        if (changed != page) {
            page = changed;
            rebuildRecipeButtons();
        }
    }

    private int maxPage() {
        return Math.max(0, (recipes.size() - 1) / RECIPES_PER_PAGE);
    }

    private void rebuildRecipeButtons() {
        for (RecipeButton button : recipeButtons) {
            removeWidget(button);
        }
        recipeButtons.clear();

        page = Math.min(page, maxPage());
        int start = page * RECIPES_PER_PAGE;
        int end = Math.min(start + RECIPES_PER_PAGE, recipes.size());
        int gridLeft = visualZone.startX() + 6;
        int gridTop = visualZone.startY() + 44;
        for (int index = start; index < end; index++) {
            RecipeOption recipe = recipes.get(index);
            int localIndex = index - start;
            int x = gridLeft + (localIndex % GRID_COLUMNS) * (RECIPE_BUTTON_SIZE + RECIPE_BUTTON_SPACING);
            int y = gridTop + (localIndex / GRID_COLUMNS) * (RECIPE_BUTTON_SIZE + RECIPE_BUTTON_SPACING);
            RecipeButton button = new RecipeButton(x, y, recipe, filterData, () -> toggleRecipe(recipe));
            recipeButtons.add(addRenderableWidget(button));
        }

        if (previousPageButton != null) {
            previousPageButton.active = page > 0;
        }
        if (nextPageButton != null) {
            nextPageButton.active = page < maxPage();
        }
    }

    private void drawScrollBar(GuiGraphics graphics) {
        int x = visualZone.startX() + 161;
        int y = visualZone.startY() + 44;
        graphics.blit(COOK_GUIDE_TEXTURE, x, y + 8, 189, 64, 9, 70);
        if (maxPage() == 0) {
            graphics.blit(COOK_GUIDE_TEXTURE, x + 1, y + 9, 206, 64, 7, 9);
            return;
        }
        int indicatorOffset = Math.round(59.0F * page / maxPage());
        graphics.blit(COOK_GUIDE_TEXTURE, x + 1, y + 9 + indicatorOffset, 199, 64, 7, 9);
    }

    private static final class RecipeButton extends TouhouStateSwitchButton {
        private final RecipeOption recipe;
        private final RecipeFilterData.Mode mode;
        private final Runnable pressAction;

        private RecipeButton(
                int x,
                int y,
                RecipeOption recipe,
                RecipeFilterData filterData,
                Runnable pressAction
        ) {
            super(x, y, RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE, filterData.contains(recipe.id()));
            initTextureValues(179, 25, 22, 0, COOK_GUIDE_TEXTURE);
            setMessage(recipe.result().getHoverName());
            this.recipe = recipe;
            this.mode = filterData.mode();
            this.pressAction = pressAction;
        }

        private RecipeOption recipe() {
            return recipe;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            pressAction.run();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
            ItemStack result = recipe.result();
            graphics.renderItem(result, getX() + 2, getY() + 2);
            int shadow = mode == RecipeFilterData.Mode.WHITELIST ? 0x50F9F9F9 : 0x50000010;
            graphics.fill(getX(), getY(), getX() + width, getY() + height, shadow);
        }
    }
}
