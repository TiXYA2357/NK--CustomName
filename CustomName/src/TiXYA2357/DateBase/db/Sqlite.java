package TiXYA2357.DateBase.db;
import TiXYA2357.DateBase.entity.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import static TiXYA2357.Main.*;

public class Sqlite {
    //这个是数据库路径 加final防止被改变
    public final String DB_PlayerInfo = "jdbc:sqlite:" + ConfigPath + "/Player.db";
    //这个是数据库连接
    public ConnectionSource comm;
    //然后有一个对象用于和数据库交互 就是Dao
    public Dao PIDao;//如果你的程序不止一个实体类和数据库表这个命名就要区分一下

    //写一个构造器 创建Sqlite对象时知道执行
    public Sqlite() {
        //这个是连接失败就会报错一般都是正常
        try {
            //连接数据库 传入DB_PATH 这个如果没有.db文件就会自己创建
            comm = new JdbcConnectionSource(DB_PlayerInfo);
            //然后可以如它自动根据刚刚写好的类创建表 没有表自己创建有就不会创建
            //传入comm和刚刚写的类Player.class
            TableUtils.createTableIfNotExists(comm, PlayerInfo.class);
            //创建Dao对象 这个要指定实体类 先创建类 也是传入comm和刚刚写的类Player.class
            PIDao = DaoManager.createDao(comm, PlayerInfo.class);
        } catch (SQLException e) {
            //我习惯打印报错信息
            nks.getLogger().info(e.getMessage());
            nks.getLogger().info("数据库连接失败");
        }

    }}