/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.dto.owntracks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 *
 * @author fadhl
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "_type", defaultImpl = Void.class)
@JsonSubTypes({
        @Type(value = Location.class, name = "location"),
        @Type(value = Lwt.class, name = "lwt"),
        @Type(value = Card.class, name = "card"),
        @Type(value = Cmd.class, name = "cmd"),
        @Type(value = Transtition.class, name = "transition"),
        @Type(value = Status.class, name = "status") })
public interface Message {
}
