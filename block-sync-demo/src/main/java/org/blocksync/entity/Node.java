package org.blocksync.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blocksync.util.GsonUtil;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    private String name;
    private String url;

    @Override
    public String toString() {
        return "[" + name + " - " + url + "]";
    }
}
