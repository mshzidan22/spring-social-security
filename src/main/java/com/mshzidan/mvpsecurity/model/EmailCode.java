package com.mshzidan.mvpsecurity.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class EmailCode {

    @Id
    @GeneratedValue
    private Long id;

    private String code;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

}
