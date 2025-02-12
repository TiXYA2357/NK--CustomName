package TiXYA2357.DateBase.entity;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "PlayerInfo")
public class PlayerInfo {//用这里定义玩家属性
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "UID")//2
    private String playerUID;
    @DatabaseField(columnName = "ID名称")//3
    private String playerIDName;
    @DatabaseField(columnName = "注册名称")//4,这里用于重写玩家命名ID以支持中文名称
    private String playerStrName;
    @DatabaseField(columnName = "是否改名")
    private boolean playerNameChanged;
}
