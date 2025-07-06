package com.enigma.lastbite.entity;

import com.enigma.lastbite.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = TableNames.CART_ITEM)
@Builder
public class CartItem {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;
}
