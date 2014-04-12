package syam.CraftIRCBridge.Bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import syam.CraftIRCBridge.CraftIRCBridge;

import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.github.ucchyocean.lc.channel.Channel;

public class BridgeEndPoint implements EndPoint {
    public final static Logger log = CraftIRCBridge.log;

    private Channel GameChannel;

    BridgeEndPoint(Channel GameChannel) {
        this.GameChannel = GameChannel;
    }

    public boolean adminMessageIn(RelayedMessage arg0) {
        return false;
    }

    public Type getType() {
        return Type.MINECRAFT;
    }

    public List<String> listDisplayUsers() {
        List<String> ret = new ArrayList<String>();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ret.add(p.getName());
        }
        return ret;
    }

    public List<String> listUsers() {
        List<String> ret = new ArrayList<String>();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ret.add(p.getName());
        }
        return ret;
    }

    public void messageIn(RelayedMessage rm) {
        if (rm == null || GameChannel == null) return;
        GameChannel.chatFromOtherSource(rm.getField("user"), "IRC", rm.getMessage(this));
    }

    public boolean userMessageIn(String user, RelayedMessage message) {
        return false;
    }

    public void messageOut(String message, String sender, String tag, String channelName, String channelNick) {
        RelayedMessage rm = CraftIRCBridge.craftIRC.newMsg(this, null, "chat");
        rm.setField("message", message);
        rm.setField("sender", sender);
        rm.setField("channelName", channelName);
        rm.setField("channelNick", channelNick);
        rm.post();
    }
}
