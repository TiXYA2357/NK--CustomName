package TiXYA2357.Command;

import TiXYA2357.DateBase.db.Sqlite;
import TiXYA2357.DateBase.entity.PlayerInfo;
import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;

import java.sql.SQLException;
import java.util.*;

import static TiXYA2357.Main.*;

public class Lib {

    public static Sqlite db = new Sqlite();

    private static PlayerInfo pi = null;
    public static Map<Player,PlayerInfo> PlayerInfoMap = new HashMap<>();
    public static void QueryPlayerUID(Player p){
        nks.getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                //异步线程查询数据库信息
                if (PlayerInfoMap.get(p) != null) return;
                try {
                    ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("UID", p.getUniqueId().toString());
                    if (PID.isEmpty()){
//                nks(PT+"玩家"+p.getName()+"信息未初始化");
                        return;}
                    pi = PID.get(0);
                    PlayerInfoMap.put(p,pi);
                }catch (SQLException e){
                    nks.getLogger().info(PT+SQLER+e);
                }
//        Delayed(() -> PlayerInfoMap.clear() ,1);
                //延迟清除缓存优化运行内存
            }
        });
    }
    public static void Updatedb(Player p){
        try {
            db.PIDao.update(PlayerInfoMap.get(p));
        } catch (SQLException e) {
            nks.getLogger().info(PT+SQLER+e);
        }
    }
    public static void onPlayerResDate(Player p){
        try {//以UUID查询对应玩家信息
            ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("UID", p.getUniqueId().toString());
            if (PID.isEmpty()){
//                //为空就是没有
                PlayerInfo pi = new PlayerInfo(null,
                        p.getUniqueId().toString(),//2玩家uuid
                        p.getName(),//3玩家Xbox名称
                        p.getName(),
                        false
                );
                db.PIDao.createIfNotExists(pi);
                nks.broadcastMessage(PT + "玩家" + p.getName() + "§7首次加入本服务器!");
                if (firstCmd.isEmpty()) return;
                for (String Cmd : firstCmd){
                    if (Cmd.contains("&op")){
                        boolean isOP = p.isOp();
                        if (!isOP) p.setOp(true);
                        nks.dispatchCommand(p, Cmd.replace("@p", p.getName()).replace("&op", ""));
                        if (!isOP) p.setOp(false);
                    }else if (Cmd.contains("&con")){
                        nks.dispatchCommand(new ConsoleCommandSender(), Cmd.replace("@p", p.getName()).replace("&con", ""));
                    }else nks.dispatchCommand(p, Cmd.replace("@p", p.getName()));
                }
            }
        }catch (SQLException es) {
            nks.getLogger().info(PT+SQLER+es);
        }
    }

    public static ArrayList<String> getPlayerStrNameList(Player p) {
        ArrayList<String> allPlayerNames = new ArrayList<>();
        for (Player serverPlayer : nks.getOnlinePlayers().values()) {
            if (p == null ||!Objects.equals(serverPlayer.getName(), p.getName())){
                allPlayerNames.add(getPlayerStrName(serverPlayer));}
        }
        return allPlayerNames;
    }public static ArrayList<String> getPlayerStrNameList() {
        return getPlayerStrNameList(null);
    }
    public static ArrayList<String> getPlayerNameList(Player p) {
        ArrayList<String> allPlayerNames = new ArrayList<>();
        for (Player serverPlayer : nks.getOnlinePlayers().values()) {
            if (p == null || !Objects.equals(serverPlayer.getName(), p.getName())){
                allPlayerNames.add(serverPlayer.getName());}
        }
        return allPlayerNames;
    }
    public static ArrayList<String> getPlayerSNameList() {
        return getPlayerNameList(null);
    }

    public static Boolean QueryPlayerStrName(String playerStrName){
        try {
            ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("注册名称", playerStrName);
            return !PID.isEmpty();
        } catch (SQLException e){
            nks.getLogger().info(PT+SQLER+e);}
        return false;
    }

    public static String getPlayerStrName(Player p){//获取玩家新名称
        try {
            ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("UID", p.getUniqueId().toString());
            String playerStrName = PID.get(0).getPlayerStrName();
            if (playerStrName != null) return playerStrName;
        } catch (SQLException e) {}

        QueryPlayerUID(p);
        return PlayerInfoMap.get(p).getPlayerStrName();
    }
    public static Boolean isNameChanged(Player p){
        QueryPlayerUID(p);
        return PlayerInfoMap.get(p).isPlayerNameChanged();
    }

    public static String getPlayerIDName(String PlayerStrName){//获取玩家真实名称
        try {
            ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("注册名称", PlayerStrName);
            if (PID.isEmpty()){
//                nks(PT+"玩家"+PlayerStrName+"不存在");
                return null;
            }
            return PID.get(0).getPlayerIDName();
        } catch (SQLException e){
            nks.getLogger().info(PT+SQLER+e);}
        return null;
    }

    public static String getPlayerUID(String PlayerStrName){//获取玩家UUID
        try {
            ArrayList<PlayerInfo> PID = (ArrayList<PlayerInfo>) db.PIDao.queryForEq("注册名称", PlayerStrName);
            if (PID.isEmpty()){
//                nks(PT+"玩家"+PlayerStrName+"不存在");
                return null;
            }
            return PID.get(0).getPlayerUID();
        } catch (SQLException e){
            nks.getLogger().info(PT+SQLER+e);}
        return null;
    }
    public static Integer getPlayerBDID(Player p){
        QueryPlayerUID(p);
        return PlayerInfoMap.get(p).getId();
    }
    public static void setPlayerStrName(Player p,String playerStrName){
        QueryPlayerUID(p);
        PlayerInfoMap.get(p).setPlayerStrName(playerStrName);
        Updatedb(p);
    }

    public static void setIsNameChange(Player p,boolean isNameChange){
        QueryPlayerUID(p);
        PlayerInfoMap.get(p).setPlayerNameChanged(isNameChange);
        Updatedb(p);
    }

    public static Boolean AllowReName = false;
    public static String TipsName = "{CustomName}";
    public static String TipsTitle = "{CustomNameCH}";
    public static String Cmd = "rename";
    public static String awto = "§b[私聊] §e@s §r§f->§e @p§r§f: §d@msg";
    public static boolean cmdoutput = true;
    private static List<String> firstCmd = new ArrayList<>();

    public static Boolean loadConfig() {
        plugin.saveResource("config.yml");
        Config config = new Config(ConfigPath + "/config.yml", Config.YAML);
        plugin.saveDefaultConfig();
        PT = config.getString("前缀","§b自定义名称 §a>>> §7");
        AllowReName = config.getBoolean("允许重命名", false);
        TipsName = config.getString("Tips名称变量","{CustomName}");
        TipsTitle = config.getString("Tips称号变量","{CustomNameCH}");
        Cmd = config.getString("指令","rename");
        awto = config.getString("私聊","§b[私聊] §e@s §r§f->§e @p§r§f: §d@msg");
        firstCmd = config.getStringList("首次进服命令");
        cmdoutput = config.getBoolean("命令输出反馈",true);
        config.save();
        config.reload();
        return true;
    }
}
