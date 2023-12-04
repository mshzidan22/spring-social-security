package com.mshzidan.mvpsecurity;

public class Admin {
    private Long id;
    private String name;
    private String password;
}


public class User {
    @Id
    private Long id;
    private String name;
    private String dateOfBirth;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;

    @OneToMany(mappedBy = "initiator")
    private Set<Initiative> initiatedInitiatives;

    @ManyToMany(mappedBy = "volunteers")
    private Set<Initiative> registeredInitiatives;

    // Constructors, getters, and setters
}
