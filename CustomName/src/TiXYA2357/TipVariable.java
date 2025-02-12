package TiXYA2357;

import TiXYA2357.DateBase.db.Sqlite;
import cn.nukkit.Player;
import org.title.TitleMain;
import org.title.data.PlayerData;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import static TiXYA2357.Command.Lib.*;
import static TiXYA2357.Main.*;


public class TipVariable extends BaseVariable {

    public TipVariable(Player player,String str) {
        super(player);
        this.string = str;
    }
    public static void init() {
        Api.registerVariables("CustomName",TipVariable.class)
        /* Api.registerVariables("TRP",TipVariable.class)*/;
    }

    boolean tips = false;
    public void strReplace() {
        try {
            addStrReplaceString(TipsName,getPlayerStrName(this.player));

            try {
                Class.forName("org.sobadfish.title.TitleMain");
                PlayerData data = TitleMain.playerManager.getData(player.getName());
                String title = TitleMain.getEmptyTitle();
                if(data.wearTitle != null) title = data.wearTitle.name;
                title = title.replace("@p",getPlayerStrName(player));
                addStrReplaceString(TipsTitle,title);
            } catch (Exception ignore) {try {
                Class.forName("org.title.TitleMain");
                PlayerData data = TitleMain.playerManager.getData(player.getName());
                String title = TitleMain.getEmptyTitle();
                if(data.wearTitle != null) title = data.wearTitle.name;
                title = title.replace("@p",getPlayerStrName(player));
                addStrReplaceString(TipsTitle,title);
            } catch (Exception ignored) {}}


        } catch (Exception e) {
            if (!tips) nks.getLogger().info(PT + "正在TIPS加载数据库变量..");
            QueryPlayerUID(this.player);
            if (db == null) db = new Sqlite();
            if (!PlayerInfoMap.containsKey(this.player)) onPlayerResDate(this.player);tips = true;
        }}
}