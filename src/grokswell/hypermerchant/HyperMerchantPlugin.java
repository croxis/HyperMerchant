package grokswell.hypermerchant;

//import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconAPI;
import regalowl.hyperconomy.HyperObjectAPI;
import regalowl.hyperconomy.HyperAPI;
import regalowl.hyperconomy.ShopFactory;
import regalowl.hyperconomy.YamlFile;

import grokswell.hypermerchant.HyperMerchantTrait;
import grokswell.hypermerchant.ShopMenu;

public class HyperMerchantPlugin extends JavaPlugin implements Listener {
	HyperEconAPI economyAPI = new HyperEconAPI();
	HyperObjectAPI objectAPI = new HyperObjectAPI();
	HyperAPI hyperAPI = new HyperAPI();
	FileConfiguration itemsyaml;
	YamlFile yaml1;
	//use "items_by_id" for reverse lookup of hyperconomy item names <id:data, name>
	HashMap<String,String> items_by_id = new HashMap<String, String>(); 
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		
		//REMOTEMENU 
		if (cmd.getName().equalsIgnoreCase("remotemenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/remotemenu");
				return true;
			}
			
			Player player = (Player) sender;
			HyperConomy hc = HyperConomy.hc;
			ShopFactory sf = hc.getShopFactory();
			ArrayList<String> shoplist = sf.listShops();
			
			if (args.length != 1) {
				sender.sendMessage(ChatColor.YELLOW+"You must specify one shop name. Example: "+
									ChatColor.RED+"/remotemenu DonutShop");
				sender.sendMessage(ChatColor.YELLOW+"Use "+ChatColor.RED+"/remoteshoplist or "+
									ChatColor.RED+"/rslist "+ChatColor.YELLOW+"for valid shop names.");
				return true;
				
			} else {
				if (shoplist.contains(args[0])) {
					new ShopMenu(args[0], 54, this, sender, player, null);
					return true;
					
				} else {
					sender.sendMessage(ChatColor.YELLOW+"Shop name not recognized. Use "+
										ChatColor.RED+"/remoteshoplist "+ChatColor.YELLOW+ 
										"or "+ChatColor.RED+"/rslist "+ChatColor.YELLOW+
										"for valid shop names. Use exact spelling.");
					return true;
				}
			}
		}
		
		// SHOPMENU 
		else if (cmd.getName().equalsIgnoreCase("shopmenu")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/shopmenu");
				return true;
			}
			
			Player player = (Player) sender;
			String name=hyperAPI.getPlayerShop(player);
			if (name.isEmpty()) {
				sender.sendMessage(ChatColor.YELLOW+"You must be standing inside " +
									"of a shop to use the command "+ChatColor.RED+"/shopmenu.");
				return true;
				
			} else {
				new ShopMenu(name, 54, this, sender, player, null);
				return true;
			}
		}
		
		//REMOTESHOPLIST
		else if (cmd.getName().equalsIgnoreCase("remoteshoplist")) {
			sender.sendMessage(ChatColor.YELLOW+"Valid shop names to use with command /remotemenu:");
			sender.sendMessage(hyperAPI.listShops());
			return true;
		}
		
		//HYPERMERCHANT
		else if (cmd.getName().equalsIgnoreCase("hmerchant")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use the command "+ChatColor.RED+"/hmerchant");
				return true;
			}
			
			//Player player = (Player) sender;
			
			if (args.length < 1) {
				//player.performCommand("help hypermerchant");
				return false;	
				
			} else {
				new HyperMerchantFunction(sender, args, this);
				return true;
			}
			
		} else {
			return true;
		}
	}
	
	


	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		HyperConomy hc2 = HyperConomy.hc;
		yaml1 = hc2.getYaml();
		itemsyaml = yaml1.getItems();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			int id = itemsyaml.getInt(name + ".information.id");
			int data = itemsyaml.getInt(name + ".information.data");
			String newkey = Integer.toString(id) +":"+ Integer.toString(data);
			items_by_id.put(newkey,name);
		}
		
		if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
			CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(HyperMerchantTrait.class).withName("hypermerchant"));
		
		} else {
			this.getLogger().info("Citizens not found. NPC hypermerchants will be disabled.");
		}


	}

}