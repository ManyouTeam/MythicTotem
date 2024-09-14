package cn.superiormc.mythictotem.hooks;

import cn.superiormc.mythicchanger.manager.MatchItemManager;
import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;
import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.user.VotingPluginUser;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.CurrencyUtils;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.math.BigDecimal;

public class PriceHook {

    public static boolean getPrice(String pluginName, String currencyName, Player player, double value, boolean take) {
        if (value < 0) {
            return false;
        }
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Your server don't have " + pluginName +
                    " plugin, but your totem config try use its hook!");
            return false;
        }
        pluginName = pluginName.toLowerCase();
        switch (pluginName) {
            case "playerpoints":
                PlayerPoints playerPoints = PlayerPoints.getInstance();
                if (playerPoints == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cCan not hook into PlayerPoints plugin, " +
                            "maybe your are using old version, please try update it to newer version!");
                    return false;
                }
                double balance = playerPoints.getAPI().look(player.getUniqueId());
                if (balance >= value) {
                    if (take) {
                        playerPoints.getAPI().take(player.getUniqueId(), (int) value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "vault":
                RegisteredServiceProvider<Economy> rsp = MythicTotem.instance.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cCan not hook into Vault plugin, " +
                            "Vault is a API plugin, maybe you didn't install a Vault-based economy plugin in your server!");
                    return false;
                }
                Economy eco = rsp.getProvider();
                if (eco.has(player, value)) {
                    if (take) {
                        eco.withdrawPlayer(player, value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "coinsengine":
                Currency currency = CoinsEngineAPI.getCurrency(currencyName);
                if (currency == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cCan not find currency " +
                            currencyName + " in CoinsEngine plugin!");
                    return false;
                }
                if (CoinsEngineAPI.getBalance(player, currency) >= value) {
                    if (take) {
                        CoinsEngineAPI.removeBalance(player, currency, value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "ultraeconomy":
                UltraEconomyAPI ueAPI = UltraEconomy.getAPI();
                if (ueAPI == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cCan not hook into UltraEconomy plugin!");
                    return false;
                }
                if (!UltraEconomy.getAPI().getCurrencies().name(currencyName).isPresent()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cCan not find currency " +
                            currencyName + " in UltraEconomy plugin!");
                    return false;
                }
                if (UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).getOnHand() >= value) {
                    if (take) {
                        UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).removeHand((float) value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "ecobits":
                if (Currencies.getByID(currencyName) == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not find currency " +
                            currencyName + " in EcoBits plugin!");
                    return false;
                }
                if (Currencies.getByID(currencyName) == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not find currency " +
                            currencyName + " in EcoBits plugin!");
                    return false;
                }
                if (CurrencyUtils.getBalance(player, Currencies.getByID(currencyName)).doubleValue() >= value) {
                    if (take) {
                        CurrencyUtils.adjustBalance(player, Currencies.getByID(currencyName), BigDecimal.valueOf(-value));
                    }
                    return true;
                } else {
                    return false;
                }
            case "peconomy":
                PEconomyAPI peAPI = PEconomyAPI.get();
                if (peAPI == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not hook into PEconomy plugin!");
                    return false;
                }
                if (peAPI.hasAmount(player.getName(), currencyName, (int) value)) {
                    if (take) {
                        WalletModel wallet = peAPI.getWallet(player.getName());
                        wallet.takeAmount(currencyName, (int) value);
                        peAPI.updateWallet(wallet);
                    }
                    return true;
                }
                else {
                    return false;
                }
            case "rediseconomy":
                RedisEconomyAPI api = RedisEconomyAPI.getAPI();
                if (api == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not hook into RedisEconomy plugin!");
                    return false;
                }
                dev.unnm3d.rediseconomy.currency.Currency redisCurrency = api.getCurrencyByName("vault");
                if (redisCurrency == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not find currency " +
                            currencyName + " in RedisEconomy plugin!");
                    return false;
                }
                if (redisCurrency.getBalance(player) >= value) {
                    if (take) {
                        redisCurrency.withdrawPlayer(player, value);
                    }
                    return true;
                }
            case "votingplugin":
                VotingPluginUser user = VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player);
                if (user == null) {
                    MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cCan not find find user data " +
                            player.getName() + " in VotingPlugin plugin!");
                    return false;
                }
                if (user.getPoints() >= (int) value) {
                    if (take) {
                        user.removePoints((int) value);
                    }
                    return true;
                }
                return false;
        }
        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You set hook plugin to "
                + pluginName + " in UI config, however for now MythicTotem does not support it!");
        return false;
    }

    public static boolean getPrice(String vanillaType,
                                   Player player,
                                   int value,
                                   boolean take) {
        vanillaType = vanillaType.toLowerCase();
        if (vanillaType.equals("exp")) {
            if (player.getTotalExperience() >= value) {
                if (take) {
                    player.giveExp(-value);
                }
                return true;
            }
            else {
                return false;
            }
        }
        else if (vanillaType.equals("levels")) {
            if (player.getLevel() >= value) {
                if (take) {
                    player.giveExpLevels(-value);
                }
                return true;
            }
            else {
                return false;
            }
        }
        MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You set economy type to "
                + vanillaType + " in UI config, however for now MythicTotem does not support it!");
        return false;
    }

    public static boolean getPrice(String pluginName,
                                   String item,
                                   Player player,
                                   int value,
                                   boolean take,
                                   ItemStack keyItems) {
        if (MythicTotem.freeVersion) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: You are using free version, " +
                    "hook item price can not be used in this version!");
            return false;
        }
        if (value < 0) {
            return false;
        }
        if (item == null) {
            return false;
        }
        int amount = 0;
        if (keyItems == null) {
            ItemStack[] storage = player.getInventory().getStorageContents();
            for (ItemStack tempVal1 : storage) {
                if (tempVal1 == null || tempVal1.getType().isAir()) {
                    continue;
                }
                ItemStack temItem = tempVal1.clone();
                temItem.setAmount(1);
                String tempVal10 = CheckValidHook.checkValid(pluginName, temItem);
                if (tempVal10 != null && tempVal10.equals(item)) {
                    amount += tempVal1.getAmount();
                }
            }
            if (amount >= value) {
                if (take) {
                    for (ItemStack itemStack : storage) {
                        if (itemStack == null || itemStack.getType().isAir()) {
                            continue;
                        }
                        ItemStack temItem = itemStack.clone();
                        temItem.setAmount(1);
                        String tempVal10 = CheckValidHook.checkValid(pluginName, temItem);
                        if (tempVal10 != null && tempVal10.equals(item)) {
                            if (itemStack.getAmount() >= value) {
                                itemStack.setAmount(itemStack.getAmount() - value);
                                break;
                            } else {
                                value -= itemStack.getAmount();
                                itemStack.setAmount(0);
                            }
                        }
                    }
                    player.getInventory().setStorageContents(storage);
                }
                return true;
            }
        }
        else {
            ItemStack temItem = keyItems.clone();
            String tempVal3 = CheckValidHook.checkValid(pluginName, temItem);
            if (tempVal3 != null && tempVal3.equals(item)) {
                amount = temItem.getAmount();
            }
            if (amount >= value) {
                if (take) {
                    keyItems.setAmount(amount - value);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean getPrice(Player player,
                                   ItemStack item,
                                   int value,
                                   boolean take,
                                   ItemStack keyItems) {
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aRequired Price Item: " + item + "!");
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §aConfirmed Key Item: " + keyItems + "!");
        }
        if (value < 0) {
            return false;
        }
        if (item == null) {
            return false;
        }
        ItemStack[] storage = player.getInventory().getStorageContents();
        int amount = 0;
        if (keyItems == null) {
            for (ItemStack tempVal1 : storage) {
                if (tempVal1 == null || tempVal1.getType().isAir()) {
                    continue;
                }
                ItemStack temItem = tempVal1.clone();
                temItem.setAmount(1);
                if (temItem.equals(item)) {
                    amount += tempVal1.getAmount();
                }
            }
            if (amount >= value) {
                if (take) {
                    for (ItemStack itemStack : storage) {
                        if (itemStack == null || itemStack.getType().isAir()) {
                            continue;
                        }
                        ItemStack temItem = itemStack.clone();
                        temItem.setAmount(1);
                        if (temItem.equals(item)) {
                            if (itemStack.getAmount() >= value) {
                                itemStack.setAmount(itemStack.getAmount() - value);
                                break;
                            } else {
                                value -= itemStack.getAmount();
                                itemStack.setAmount(0);
                            }
                        }
                    }
                    player.getInventory().setStorageContents(storage);
                }
                return true;
            }
        }
        else {
            ItemStack temItem = keyItems.clone();
            if (temItem.equals(item)) {
                amount = temItem.getAmount();
                if (amount >= value) {
                    if (take) {
                        keyItems.setAmount(amount - value);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean getPrice(Player player, ConfigurationSection section, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storage = player.getInventory().getStorageContents();
        int amount = 0;
        for (ItemStack tempVal1 : storage) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            ItemStack temItem = tempVal1.clone();
            temItem.setAmount(1);
            if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), temItem)) {
                amount += tempVal1.getAmount();
            }
        }
        if (amount >= value) {
            if (take) {
                for (ItemStack itemStack : storage) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    ItemStack temItem = itemStack.clone();
                    temItem.setAmount(1);
                    if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), temItem)) {
                        if (itemStack.getAmount() >= value) {
                            itemStack.setAmount(itemStack.getAmount() - value);
                            break;
                        } else {
                            value -= itemStack.getAmount();
                            itemStack.setAmount(0);
                        }
                    }
                }
                player.getInventory().setStorageContents(storage);
            }
            return true;
        }
        else {
            return false;
        }
    }
}
