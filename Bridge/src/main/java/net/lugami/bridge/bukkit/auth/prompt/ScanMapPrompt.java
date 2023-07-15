package net.lugami.bridge.bukkit.auth.prompt;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.minecraft.util.org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.auth.TotpMapCreator;
import net.lugami.bridge.global.profile.Profile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class ScanMapPrompt extends StringPrompt {

    private static final String  totptImageUrlFormat = "https://www.google.com/chart?chs=130x130&chld=M%%7C0&cht=qr&chl=%s";
    private static final TotpMapCreator totpMapCreator = new TotpMapCreator();
    private static final Base32 base32Encoder = new Base32();
    private static SecureRandom secureRandom;
    int failures = 0;

    public String getPromptText(ConversationContext context) {
        Player player = (Player) context.getForWhom();
        if(this.failures == 0) {
            Bukkit.getScheduler().runTaskAsynchronously(Bridge.getInstance(), () -> {
               String totpSecret = generateTotpSecret();
                BufferedImage totpImage = createTotpImage(player, totpSecret);
                Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
                    ItemStack map = totpMapCreator.createMap(player, totpImage);
                    context.setSessionData("totpSecret", totpSecret);
                    context.setSessionData("totpMap", map);
                    player.getInventory().addItem(map);
                });
            });
        }

        return ChatColor.RED + "On your 2FA device, scan the map given to you. Once you've scanned the map, type the code displayed on your device in chat.";
    }

    public Prompt acceptInput(ConversationContext context, String s) {
        Player player = (Player) context.getForWhom();
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());
        ItemStack totpMap = (ItemStack) context.getSessionData("totpMap");
        player.getInventory().remove(totpMap);
        String totpSecret = (String) context.getSessionData("totpSecret");

        if(this.failures >= 3) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Cancelling 2FA setup due to too many incorrect codes.");
            context.getForWhom().sendRawMessage(ChatColor.RED + "Contact the Bridge Network staff team for any questions you may have about 2FA.");
            return Prompt.END_OF_CONVERSATION;
        }

        int totpCode;
        try {
            totpCode = Integer.parseInt(s.replaceAll(" ", ""));
        } catch(NumberFormatException ex) {
            context.getForWhom().sendRawMessage(" ");
            context.getForWhom().sendRawMessage(ChatColor.RED + s + " isn't a valid totp code. Let's try that again");
            return this;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Bridge.getInstance(), () -> {

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            boolean isValid = gAuth.authorize(totpSecret, totpCode);

            if(isValid) {
                profile.setSecretKey(totpSecret);
                profile.saveProfile();

                player.removeMetadata("Locked", Bridge.getInstance());

                player.sendMessage(ChatColor.GREEN + "2FA setup completed successfully.");
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + s + " isn't a valid totp code. Let's try that again");
                failures++;
            }

        });
        return Prompt.END_OF_CONVERSATION;
    }

    private static String generateTotpSecret() {
        byte[] secretKey = new byte[10];
        secureRandom.nextBytes(secretKey);
        return base32Encoder.encodeToString(secretKey);
    }

    private static BufferedImage createTotpImage(Player player, String secret) {
        Escaper urlEscaper = UrlEscapers.urlFragmentEscaper();
        String totpUrl = "otpauth://totp/" + urlEscaper.escape(player.getName()) + "?secret=" + secret + "&issuer=" + urlEscaper.escape("Bridge Network");
        String totpImageUrl = String.format(totptImageUrlFormat, URLEncoder.encode(totpUrl));

        try {
            return ImageIO.read(new URL(totpImageUrl));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
        }

    }

}
