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
        
        getLogger().info("Самопис FrostJack успешно запущен!");
    }

    private void checkHelmetEffects(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.JACK_O_LANTERN && helmet.hasItemMeta() && ITEM_NAME.equals(helmet.getItemMeta().getDisplayName())) {
            if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
                
                player.sendMessage("§6§lFROSTWORLD §8» §eВы надели §dГолову Джека§e! Сила Хэллоуина активирована.");
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.4f);
                player.getWorld().spawnParticle(Particle.SPELL_WITCH, player.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.1);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
            }
        } else {
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) && player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getDuration() <= 100) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.sendMessage("§6§lFROSTWORLD §8» §7Эффекты Головы Джека рассеялись.");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("jack.admin")) {
            sender.sendMessage("§cУ вас нет прав!");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("§cИспользуйте: /jackgive <ник>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return true;
        }

        ItemStack jack = new ItemStack(Material.JACK_O_LANTERN);
        ItemMeta meta = jack.getItemMeta();
        meta.setDisplayName(ITEM_NAME);

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);

        AttributeModifier speedMod = new AttributeModifier(UUID.fromString("33333333-3333-3333-3333-333333333333"), "jack_speed", 0.20, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HEAD);
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedMod);

        AttributeModifier healthMod = new AttributeModifier(UUID.fromString("11111111-1111-1111-1111-111111111111"), "jack_health", 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthMod);

        AttributeModifier toughnessMod = new AttributeModifier(UUID.fromString("22222222-2222-2222-2222-222222222222"), "jack_toughness", 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessMod);

        meta.setLore(Arrays.asList(
            "§7",
            "§fЭксклюзивный предмет сервера §bFrostWorld",
            "§7",
            "§6⚡ Пассивный эффект:",
            " §7• §cСила I (При ношении)",
            "§7"
        ));
        jack.setItemMeta(meta);

        target.getInventory().addItem(jack);
        sender.sendMessage("§6§lFrostWorld §8» §eВыдали предмет игроку §b" + target.getName());
        return true;
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            ItemStack stack = item.getItemStack();
            if (stack.getType() == Material.JACK_O_LANTERN && stack.hasItemMeta() && ITEM_NAME.equals(stack.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler public void onInv(InventoryClickEvent e) { if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getWhoClicked() instanceof Player p) new BukkitRunnable(){@Override public void run(){checkHelmetEffects(p);}}.runTaskLater(this, 1L); }
    @EventHandler public void onInteract(PlayerInteractEvent e) { if (e.getItem() != null && e.getItem().getType() == Material.JACK_O_LANTERN) new BukkitRunnable(){@Override public void run(){checkHelmetEffects(e.getPlayer());}}.runTaskLater(this, 1L); }
}