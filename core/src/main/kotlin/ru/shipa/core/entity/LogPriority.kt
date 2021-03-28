package ru.shipa.core.entity

/**
 * System log priority.
 *
 * @author v.shipugin
 */
enum class LogPriority {
    EMERGENCY,
    ALERT,
    CRITICAL,
    ERROR,
    WARNING,
    NOTICE,
    INFORMATIONAL,
    DEBUG
}