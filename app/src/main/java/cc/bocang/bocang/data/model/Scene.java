package cc.bocang.bocang.data.model;

/**
 * Created by thinkpad on 2016/8/19.
 */
public class Scene {
    private String id;//场景的ID
    private String name;//场景的名称
    private String path;//场景URL路径名
    private String sort;//排序
    private String is_best;//是否推荐，0为不是，1为是
    private String click;//点击次数
    private String icon_paht;
    private String scene_desc;

    public Scene() {

    }

    public Scene(String id, String name, String path, String scene_desc) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.scene_desc = scene_desc;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sort='" + sort + '\'' +
                ", is_best='" + is_best + '\'' +
                ", click='" + click + '\'' +
                ", icon_paht='" + icon_paht + '\'' +
                ", scene_desc='" + scene_desc + '\'' +
                '}';
    }

    public String getScene_desc() {
        return scene_desc;
    }

    public void setScene_desc(String scene_desc) {
        this.scene_desc = scene_desc;
    }

    public String getIcon_paht() {
        return icon_paht;
    }

    public void setIcon_paht(String icon_paht) {
        this.icon_paht = icon_paht;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIs_best() {
        return is_best;
    }

    public void setIs_best(String is_best) {
        this.is_best = is_best;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }
}
