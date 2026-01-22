package org.rebelland.pipisa.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class MenuGiveAnItem extends Menu {

    //----КОНСТРУКТОР МЕНЮШКИ------------------------------------------------------
    public MenuGiveAnItem(){
        setTitle(Common.colorize("&e&lМЕНЮ БЕСПЛАТНЫХ ДЕНЕГ"));
        setSize(9 * 3);
    }
    //----КОНСТРУКТОР МЕНЮШКИ------------------------------------------------------

    //----КНОПОЧКА
    private final Button myButtonGetItem = new Button() {
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType click) {
            // Проверка предмета в конкретном слоте
            ItemStack itemCheck = player.getInventory().getItem(4);
            if (itemCheck != null && itemCheck.getType() != Material.AIR) {
                player.sendMessage(Common.colorize("&cОшибка: &4Вытряхивай пятый слот."));
                player.closeInventory();
            }
            else {
                ItemStack ItemFREEMoney = ItemCreator.of(CompMaterial.SUNFLOWER)
                                .amount(52)
                                .name(Common.colorize("&6&lКУЧА МОНЕТОК"))
                                .lore(Common.colorize("&e&lвсмысле подсолнух?"))
                                .make();
                player.getInventory().setItem(4, ItemFREEMoney);
                player.closeInventory();
            }
        }
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.GOLD_BLOCK)
                    .name(Common.colorize("&6&lЧЕСТНЫЕ МОНЕТКИ"))
                    .lore(Common.colorize("&e&lАБСОЛЮТНО БЕСПЛАТНО, ПРОСТО НАЖМИ НА МЕНЯ"))
                    .make();
        }
    };
    //----КНОПОЧКА


    //----ЗАПОЛНЕНИЕ СЛОТОВ МЕНЮШКИ------------------------------------------------
    @Override
    public ItemStack getItemAt( int slot ) {
        if (slot == 13) {
            return myButtonGetItem.getItem();
        }
        else if (slot % 2 == 0){
            return ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE)
                    .name(" ")
                    .make();
        }
        return ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE)
                .name(" ")
                .make();
    }
    //----ЗАПОЛНЕНИЕ СЛОТОВ МЕНЮШКИ------------------------------------------------
}
