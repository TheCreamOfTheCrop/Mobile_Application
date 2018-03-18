package ecalle.com.bmybank.realm.bo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 14/03/2018.
 */
open class User(id: Int = 0,
                uid: String = "",
                email: String = "",
                password: String = "",
                lastname: String = "",
                firstname: String = "",
                avatar: String = "",
                description: String = "",
                isAccountValidate: Boolean = true) : RealmObject(), Serializable
{
    @PrimaryKey
    open var id: Int = 0
    open var uid: String = ""
    open var email: String = ""
    open var password: String = ""
    open var lastname: String = ""
    open var firstname: String = ""
    open var avatar: String? = null
    open var description: String? = null
    open var isAccountValidate: Boolean = true

    init
    {
        this.id = id
        this.uid = uid
        this.email = email
        this.password = password
        this.lastname = lastname
        this.firstname = firstname
        this.avatar = avatar
        this.description = description
        this.isAccountValidate = isAccountValidate
    }
}