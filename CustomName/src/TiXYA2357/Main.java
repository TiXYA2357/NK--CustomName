package TiXYA2357;

import TiXYA2357.Command.AdminNameCommand;
import TiXYA2357.DateBase.db.Sqlite;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;

import static TiXYA2357.Command.Lib.*;


/**
 * @author TiXYA2357
 * @version 1.1.4
 * <p>
 * Time : 2025/1/31 23:41
 * <p>
 * {@code 代码文本}
 * <p>
 * {@link TiXYA2357.DateBase}
 */
public class Main extends PluginBase implements Listener {
    public static Plugin plugin;
    public static String ConfigPath;
    public static Server nks = Server.getInstance();
    public static String SQLER = "§c数据库错误: ";
    public static String PT = "§b自定义名称 §a>>> §7";

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getCommandMap().register("CustomName", new AdminNameCommand(Cmd));
        getServer().getLogger().info("§a[自定义玩家名称] §e插件开启   §a作者§f: §bTiXYA2357");
    }

    public void onLoad() {
        plugin = this;
        ConfigPath = this.getDataFolder().getPath();
        new Config(ConfigPath + "/temp.yml", Config.YAML);
        if (db == null) db = new Sqlite();
        loadConfig();try{
        Class.forName("tip.utils.variables.BaseVariable");
        TipVariable.init();
        new File(ConfigPath + "/temp.yml").delete();
        }catch (Exception ignored){}}
    @EventHandler
    public void onPlayerLocallyInitializedEvent(PlayerLocallyInitializedEvent e) {
        Player p = e.getPlayer();
        QueryPlayerUID(p);
        onPlayerResDate(p);
    }
    }
