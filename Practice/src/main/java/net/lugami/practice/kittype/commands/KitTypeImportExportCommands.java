package net.lugami.practice.kittype.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.qLib;
import net.lugami.practice.Practice;
import net.lugami.practice.kittype.KitType;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KitTypeImportExportCommands {

	@Command(names = "kittype export", permission = "op", async = true)
	public static void executeExport(CommandSender sender) {
		String json = qLib.PLAIN_GSON.toJson(KitType.getAllTypes());

		try {
			Files.write(
					json,
					new File(Practice.getInstance().getDataFolder(), "kitTypes.json"),
					Charsets.UTF_8
			);

			sender.sendMessage(ChatColor.GREEN + "Exported.");
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Failed to export.");
		}
	}

	@Command(names = "kittype import", permission = "op", async = true)
	public static void executeImport(CommandSender sender) {
		File file = new File(Practice.getInstance().getDataFolder(), "kitTypes.json");

		if (file.exists()) {
			try (Reader schematicsFileReader = Files.newReader(file, Charsets.UTF_8)) {
				Type schematicListType = new TypeToken<List<KitType>>() {}.getType();
				List<KitType> kitTypes = qLib.PLAIN_GSON.fromJson(schematicsFileReader, schematicListType);

				for (KitType kitType : kitTypes) {
					KitType.getAllTypes().removeIf(otherKitType -> otherKitType.id.equals(kitType.id));
					KitType.getAllTypes().add(kitType);
					kitType.saveAsync();
				}
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to import.");
			}
		}

		sender.sendMessage(ChatColor.GREEN + "Imported.");
	}

}
