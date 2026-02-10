package com.multipos.core.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val avatar: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val createdAt: String,
    val updatedAt: String
)

enum class UserRole {
    ADMIN,
    STORE_MANAGER,
    CASHIER,
    INVENTORY_STAFF;

    fun hasPermission(permission: Permission): Boolean {
        return when (this) {
            ADMIN -> true
            STORE_MANAGER -> permission in listOf(
                Permission.MANAGE_STORE,
                Permission.VIEW_INVENTORY,
                Permission.VIEW_BILLING,
                Permission.CREATE_REPORT
            )
            CASHIER -> permission in listOf(
                Permission.VIEW_INVENTORY,
                Permission.PROCESS_PAYMENT,
                Permission.VIEW_BILLING
            )
            INVENTORY_STAFF -> permission in listOf(
                Permission.VIEW_INVENTORY,
                Permission.UPDATE_STOCK
            )
        }
    }
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}

enum class Permission {
    MANAGE_STORE,
    MANAGE_USERS,
    VIEW_INVENTORY,
    UPDATE_STOCK,
    PROCESS_PAYMENT,
    VIEW_BILLING,
    CREATE_REPORT
}