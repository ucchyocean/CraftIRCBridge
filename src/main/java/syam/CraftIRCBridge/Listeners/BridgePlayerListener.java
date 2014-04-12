package syam.CraftIRCBridge.Listeners;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import syam.CraftIRCBridge.CraftIRCBridge;
import syam.CraftIRCBridge.Bridge.Bridge;
import syam.CraftIRCBridge.Bridge.BridgeManager;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;

public class BridgePlayerListener implements Listener {
    public final static Logger log = CraftIRCBridge.log;

    /* 登録するイベントはここから下に */

    /**
     * コマンド実行イベント
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onServerCommand(final ServerCommandEvent event) {
        String cmd = event.getCommand();
        if (cmd.length() > 4 && cmd.toLowerCase().startsWith("say")) {
            String message = event.getCommand().substring(4, event.getCommand().length());
            postIRConce("[Server]", message); // キャスト
        }
        if (cmd.length() > 6 && cmd.toLowerCase().startsWith("bcast")) {
            String message = event.getCommand().substring(6, event.getCommand().length());
            postIRConce("[Server]", message); // キャスト
        }
        if (cmd.length() > 12 && cmd.toLowerCase().startsWith("admin bcast")) {
            String message = event.getCommand().substring(12, event.getCommand().length());
            postIRConce("[sys]", message); // キャスト
        }
    }

    /**
     * チャンネルチャットイベント [HeroChat]
     *
     * @param e
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChannelChat(final LunaChatChannelChatEvent e) {

        String message = e.getNgMaskedMessage();
        String c = e.getChannelName();
        Channel channel = LunaChat.getInstance().getLunaChatAPI().getChannel(c);

        for (Bridge b : BridgeManager.getBridges(channel)) {
            b.endPoint.messageOut(message, e.getPlayer().getName(), b.craftIRCTag, c, c);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player p = event.getPlayer();
        postIRConce("*** ", "'" + p.getDisplayName() + "' さんが接続しました！ (" + getLocString(p.getLocation()) + ")");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player p = event.getPlayer();
        postIRConce("*** ", "'" + event.getPlayer().getDisplayName() + "' さんが切断しました！ (" + getLocString(p.getLocation()) + ")");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKick(final PlayerKickEvent event) {
        Player p = event.getPlayer();
        postIRConce("*** ", "'" + p.getDisplayName() + "' はKickされました:[" + event.getReason() + "][" + p.getAddress() + "]");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player p = event.getEntity();
        postIRConce("** ", "'" + p.getDisplayName() + "' が死にました！ (" + getLocString(p.getLocation()) + ")");
    }

    private void postIRConce(String sender, String message) {
        for (Bridge b : BridgeManager.bridges) {
            b.endPoint.messageOut(message, sender, b.craftIRCTag, b.GameChannel.getName(), b.GameChannel.getName());
            break; // 同じIRCチャンネルなので1回のみのキャストにする
        }
    }

    private String getLocString(Location loc) {
        return loc.getWorld().getName() + ": " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }
}
