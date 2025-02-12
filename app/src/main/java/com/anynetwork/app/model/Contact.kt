package com.anynetwork.app.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
@Stable
data class Contact(
    val id: Long,
    val name: String,
    val phones: List<Phone> = listOf(),
    val emails: List<Email> = listOf(),
    val avatarUri: String? = null,
    val isFavorite: Boolean = false,
    val company: String? = null,
    val title: String? = null,
    val department: String? = null,
    val priority: Int? = 0,
) {
    fun getDisplayNameFirstLetters(): String {
        return name.split(" ")
            .filter { it.isNotEmpty() }
            .map { it[0] }
            .joinToString("")
            .take(2)
    }

    fun firstName() = name.split(" ")[0]

    fun lastName() = name
        .split(" ")
        .drop(1)
        .joinToString(" ")

    fun mobilePhone() = phones.firstOrNull { it.type == Phone.Type.Mobile }?.value

    fun homePhone() = phones.firstOrNull { it.type == Phone.Type.Home }?.value

    fun workPhone() = phones.firstOrNull { it.type == Phone.Type.Work }?.value

    fun mainPhone() = phones.firstOrNull { it.type == Phone.Type.Main }?.value

    fun workFax() = phones.firstOrNull { it.type == Phone.Type.WorkFax }?.value

    fun homeFax() = phones.firstOrNull { it.type == Phone.Type.HomeFax }?.value

    fun pager() = phones.firstOrNull { it.type == Phone.Type.Pager }?.value

    fun otherPhone() = phones.firstOrNull { it.type == Phone.Type.Other }?.value

    fun homeEmail() = emails.firstOrNull { it.type == Email.Type.Home }?.value

    fun workEmail() = emails.firstOrNull { it.type == Email.Type.Work }?.value

    fun otherEmail() = emails.firstOrNull { it.type == Email.Type.Other }?.value

    @Serializable data class Phone(val type: Type, val value: String) {
        @Serializable sealed class Type {
            data object Mobile: Type()
            data object Home: Type()
            data object Work: Type()
            data object Main: Type()
            data object WorkFax: Type()
            data object HomeFax: Type()
            data object Pager: Type()
            data object Other: Type()
        }
    }

    @Serializable data class Email(val type: Type, val value: String) {
        @Serializable sealed class Type {
            data object Home: Type()
            data object Work: Type()
            data object Other: Type()
        }
    }

    val phone: String?
        get() { return phones.firstOrNull()?.value }

    val email: String?
        get() { return emails.firstOrNull()?.value }

    override fun equals(other: Any?): Boolean {
        if (other !is Contact) return false

        return id == other.id &&
                name == other.name &&  // Compare the actual name
                phones == other.phones && // Compare the actual phones list
                emails == other.emails && // Compare the actual emails list
                avatarUri == other.avatarUri &&
                isFavorite == other.isFavorite &&
                company == other.company &&
                title == other.title &&
                department == other.department &&
                priority == other.priority
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phones.hashCode()
        result = 31 * result + emails.hashCode()
        result = 31 * result + (avatarUri?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + (company?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (department?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        return result
    }
}