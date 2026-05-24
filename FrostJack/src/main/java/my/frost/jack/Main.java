package my.frost.jack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {
    private final String ITEM_NAME = "§dГолова Джека";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("jackgive").setExecutor(this);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkHelmetEffects(player);
                }
            }
        }.runTaskTimer(this, 20L, 20L);
        
        getLogger().info("Самопис FrostJack с круглой 3D-головой успешно запущен!");
    }

    private void checkHelmetEffects(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        // Отслеживаем круглую PLAYER_HEAD по имени
        if (helmet != null && helmet.getType() == Material.PLAYER_HEAD && helmet.hasItemMeta() && ITEM_NAME.equals(helmet.getItemMeta().getDisplayName())) {
            if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
                
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.4f);
                player.getWorld().spawnParticle(Particle.SPELL_WITCH, player.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.1);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
            }
        } else {
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) && player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getDuration() <= 100) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("jack.admin")) return true;
        if (args.length != 1) return true;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) return true;

        // Железобетонный чит-код: выдаём круглую 3D-тыкву с текстурой РВ через скрытую консольную команду!
        String textureCode = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3OGY5ZDU0YzRkYzk2N2UzNTI1YmQxYjI1YTNhNTEzNzZkMTNjYWFjNjVlYmU2YmU1ZGM3NTkyMWYifX19";
        String giveCommand = "minecraft:give " + target.getName() + " player_head{SkullOwner:{Id:[I;1,2,3,4],Properties:{textures:[{Value:\"" + textureCode + "\"}]}},display:{Name:'{\"text\":\"§dГолова Джека\",\"italic\":false}',Lore:['{\"text\":\"\"}','{\"text\":\"§fЭксклюзивный предмет сервера §bFrostWorld\",\"italic\":false}','{\"text\":\"\"}','{\"text\":\"§6⚡ Пассивный эффект:\",\"italic\":false}','{\"text\":\" §7• §cСила I (При ношении)\",\"italic\":false}','{\"text\":\"\"}']},Enchantments:[{id:\"minecraft:protection\",lvl:4},{id:\"minecraft:vanishing_curse\",lvl:1}],AttributeModifiers:[{AttributeName:\"generic.max_health\",Name:\"jack_hp\",Amount:6.0,Operation:0,UUID:[I;11,11,11,11],Slot:\"head\"},{AttributeName:\"generic.armor_toughness\",Name:\"jack_tough\",Amount:2.0,Operation:0,UUID:[I;22,22,22,22],Slot:\"head\"},{AttributeName:\"generic.movement_speed\",Name:\"jack_speed\",Amount:0.20,Operation:1,UUID:[I;33,33,33,33],Slot:\"head\"}]} 1";
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveCommand);
        return true;
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            ItemStack stack = item.getItemStack();
            if (stack.getType() == Material.PLAYER_HEAD && stack.hasItemMeta() && ITEM_NAME.equals(stack.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler public void onInv(InventoryClickEvent e) { if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getWhoClicked() instanceof Player p) new BukkitRunnable(){@Override public void run(){checkHelmetEffects(p);}}.runTaskLater(this, 1L); }
    @EventHandler public void onInteract(PlayerInteractEvent e) { if (e.getItem() != null && e.getItem().getType() == Material.PLAYER_HEAD) new BukkitRunnable(){@Override public void run(){checkHelmetEffects(e.getPlayer());}}.runTaskLater(this, 1L); }
}
