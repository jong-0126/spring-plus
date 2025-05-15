package org.example.expert.domain.log.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Setter;
import org.example.expert.domain.common.entity.Timestamped;

import lombok.Getter;

@Setter
@Getter
@Entity
@Table(name = "log")
public class Log {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request_time", nullable = false)
	private LocalDateTime requestTime;

	@Column(name = "action", nullable = false)
	private String action;

	@Column(name = "message")
	private String message;

	@Column(name = "user_id")
	private Long userId;

}