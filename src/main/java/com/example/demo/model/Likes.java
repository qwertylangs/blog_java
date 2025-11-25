//package com.example.demo.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "likes")
//public class Likes {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch= FetchType.LAZY)
//    @JoinColumn(name = "article_id")
//    private int articleid;
//
//    @ManyToOne(fetch= FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private int userid;
//}
