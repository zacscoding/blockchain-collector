package org.blocksync.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zacconding
 * @Date 2018-05-18
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pair<F, S> {

    private F first;
    private S second;
}
