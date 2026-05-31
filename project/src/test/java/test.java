public class test {
    public static void main(String[] args) {
        String sql = """
                ALTER TABLE t_link_%d ADD COLUMN total_uv INT DEFAULT 0 COMMENT '历史uv' after `description` ,
                                                                         ADD COLUMN total_pv INT DEFAULT 0 COMMENT '历史pv' after total_uv ,
                                                                         ADD COLUMN total_uip INT DEFAULT 0 COMMENT '历史uip' after total_pv ;""";
        for (int i = 0; i < 16; i++) {
            System.out.printf((sql) + "%n", i);
        }
    }
}
