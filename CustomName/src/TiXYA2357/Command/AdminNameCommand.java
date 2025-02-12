package TiXYA2357.Command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.regex.Pattern;

import static TiXYA2357.Main.*;
import static TiXYA2357.Command.Lib.*;

public class AdminNameCommand extends Command {
    public AdminNameCommand(String str) {
        super(str, "重置玩家名称");
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player p){
            FormWindowSimple form = new FormWindowSimple("重置玩家名称","");
            if (PlayerInfoMap.get(p) == null) {p.sendMessage(PT+"数据还没有被加载,请重新打开UI"); QueryPlayerUID(p); return false; }
            if (p.isOp() || !isNameChanged(p)) form.addButton(new ElementButton("注册名称"));
            else {form.setContent("§c你已注册过名称了");}
            form.addButton(new ElementButton("私聊玩家"));
            if (p.isOp()) {
                form.addButton(new ElementButton("设置名称"));
                form.addButton(new ElementButton("执行指令"));
                form.addButton(new ElementButton("重载配置"));
                form.addButton(new ElementButton("解除限制"));
                form.addButton(new ElementButton("接口说明"));
                form.addButton(new ElementButton("实际信息"));
            }
            form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
                if (form.wasClosed()) return;
                switch (form.getResponse().getClickedButton().getText()){
                    case "注册名称":
                        form_login_name(p);
                        break;
                    case "私聊玩家":
                        form_awto(p);
                        break;
                    case "设置名称":
                        form_set_name(p);
                        break;
                    case "执行指令":
                        form_execmd(p);
                        break;
                    case "解除限制":
                        form_allow_ReName(p);
                        break;
                    case "重载配置":
                        if (loadConfig()) p.sendMessage(PT+"配置文件重载成功");
                        else p.sendMessage(PT+"配置文件重载失败");
                        break;
                    case "接口说明":
                        p.sendMessage(PT+ """
                                        
                                        getPlayerStrNameList(Player p) 数组,获取全服玩家并且排除p的更改名称
                                        getPlayerStrNameList() 数组,获取全服玩家的更改名称
                                        getPlayerNameList(Player p) 数组,获取全服玩家并且排除p的原版名称
                                        getPlayerNameList() 数组,获取全服玩家原版名称
                                        getPlayerIDName(String PlayerStrName) 字符串,通过更改名称获取玩家原版名称
                                        getPlayerUID(String PlayerStrName) 字符串,通过更改名称获取玩家UUID
                                        getPlayerBDID(Player p) 整数,获取玩家数据ID
                                        QueryPlayerStrName() 布尔值,用于查询玩家更改名称是否存在(同名检查)
                                        isNameChanged() 布尔值,用于判断玩家是否更改过名称
                                        setPlayerStrName(Player p,String playerStrName) 设置玩家自定义名称
                                        setIsNameChange(Player p,boolean isNameChange) 设置玩家是否更改过名称
                                        """);
                        break;
                    case "实际信息":
                        form_vainfo(p);
                        break;
                    default:
                        break;
                }}));
            p.showFormWindow(form);
        }
        return true;
    }
    public static void form_execmd(Player p){
        FormWindowCustom form = new FormWindowCustom("§d执行指令");
        form.addElement(new ElementDropdown("§d选择在线玩家: ", getPlayerStrNameList()));
        form.addElement(new ElementInput("§9输入指令: ", "用@p表示选择的玩家"));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            String playerName = getPlayerIDName(form.getResponse().getDropdownResponse(0).getElementContent());
            if (playerName != null) {
                nks.dispatchCommand(p, form.getResponse().getInputResponse(1).replace("@p",playerName));
                if (cmdoutput) p.sendMessage(PT+ "指令执行成功:§e " + form.getResponse().getInputResponse(1).replace("@p",playerName));
            }
        }));
        p.showFormWindow(form);
    }
    public static void form_awto(Player p){
        FormWindowCustom form = new FormWindowCustom("§d私聊玩家");
        form.addElement(new ElementDropdown("§d选择在线玩家: ", getPlayerStrNameList(p)));
        form.addElement(new ElementInput("§9输入私聊内容: "));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            try {
                String playerName = form.getResponse().getDropdownResponse(0).getElementContent();
                String inputText = form.getResponse().getInputResponse(1); // 获取文本输入框的值
                if (!inputText.isEmpty()) {
                Player target = nks.getPlayer(getPlayerIDName(playerName));
                p.sendMessage(awto.replace("@s","你").replace("@p",getPlayerStrName(target)).replace("@msg",inputText));//私聊发送者
                target.sendMessage(awto.replace("@s",getPlayerStrName(p)).replace("@p","你").replace("@msg",inputText));//私聊接收者
                nks.getLogger().info(awto.replace("@s",getPlayerStrName(p)).replace("@p",getPlayerStrName(target)).replace("@msg",inputText));//后台
                for (Player op : nks.getOnlinePlayers().values()){if (op.isOp() && !op.getName().equals(p.getName()) && !op.getName().equals(target.getName())){
                op.sendMessage(awto.replace("@s",getPlayerStrName(p)).replace("@p",getPlayerStrName(target)).replace("@msg",inputText));//管理员
                }}}else p.sendMessage(PT+"请输入私聊内容");
            }catch (Exception ignored1){}}));
        if (nks.getOnlinePlayers().values().size() == 1) p.sendMessage(PT+"当前没有可私聊的其它玩家"); else p.showFormWindow(form);

    }
    public static void form_vainfo(Player p) {
        FormWindowCustom form = new FormWindowCustom("§d实际信息");
        form.addElement(new ElementDropdown("§d选择玩家", getPlayerStrNameList()));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            String name2 = form.getResponse().getDropdownResponse(0).getElementContent();
            Player player = nks.getPlayer(getPlayerIDName(name2));
            String info = "§d玩家信息\n" +
                    "§d玩家UUID: §e" + player.getUniqueId() + "\n" +
                    "§d玩家名称: §e" + player.getName() + "\n" +
                    "§d玩家自定义名称: §e" + getPlayerStrName(player) + "\n" +
                    "§d玩家是否更改过名称: §e" + isNameChanged(player) + "\n" +
                    "§d玩家数据库ID: §e" + getPlayerBDID(player) + "\n";
            p.sendMessage(PT+ info);
        }));
        p.showFormWindow(form);
    }
    public static void form_allow_ReName(Player p) {
        FormWindowCustom form = new FormWindowCustom("§d解除改名限制");
        form.addElement(new ElementDropdown("§d选择玩家", getPlayerStrNameList()));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            String name = form.getResponse().getDropdownResponse(0).getElementContent();
            setIsNameChange(nks.getPlayer(getPlayerIDName(name)),false);
            p.sendMessage(PT+"已解除 " + name + " 的改名限制");
        }));
        p.showFormWindow(form);
    }

    public static void form_login_name(Player p) {
        form_login_name(p,true);}
    public static void form_login_name(Player p,boolean is_login) {
        FormWindowCustom form = new FormWindowCustom("§d注册名字");
        form.addElement(new ElementInput("§d输入昵称(2~8个字)","确定后无法更改,请谨慎!"));
        if (!is_login) form.addElement(new ElementLabel("§c昵称不合法或重名" + ",请输入2~8个字符"));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            String name = form.getResponse().getInputResponse(0);
            if (isValidName(name) && !name.equals(" ")
                    && !name.toLowerCase().equals("null")){
                if (QueryPlayerStrName(name)) {
                    form_login_name(p,false);return;}
                p.sendMessage(PT+"游戏名称设置成功: " + name);
                 setPlayerStrName(p, name);
                if (!AllowReName) setIsNameChange(p, true);
            } else {
                form_login_name(p,false); // 重新显示表单
            }
        }));
        p.showFormWindow(form);
    }
    public static void form_set_name(Player p) {
        form_set_name(p,true);}
    public static void form_set_name(Player p,boolean is_set) {
        FormWindowCustom form = new FormWindowCustom("§d修改名字");
        form.addElement(new ElementInput("§e输入新昵称(2~8个字)"));
        form.addElement(new ElementDropdown("§d选择玩家", getPlayerStrNameList()));
        if (!is_set) form.addElement(new ElementLabel("§c昵称不合法或重名" + ",请输入2~8个字符"));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            String name =  form.getResponse().getInputResponse(0);
            String name2 = form.getResponse().getDropdownResponse(1).getElementContent();
            Player player = nks.getPlayer(getPlayerIDName(name2));
            if (isValidName(name) && !name.equals(" ")
                    && !name.toLowerCase().equals("null")){
                if (QueryPlayerStrName(name)) {
                    form_set_name(player,false);return;}
                p.sendMessage(PT+"游戏名称设置成功: §f" + name);
                setPlayerStrName(player, name);
                if (!AllowReName) setIsNameChange(player, true);
            } else {
                form_set_name(player,false); // 重新显示表单
            }


        }));
        p.showFormWindow(form);
    }
    private static boolean isValidName(String name) {
        return Pattern.compile("^[a-zA-Z\\u4e00-\\u9fa5_-]{2,8}$").matcher(name).matches();
    }
}
