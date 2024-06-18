import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val PREF_FIRST_INSTALL = "PREF_FIRST_INSTALL"
    private val PREF_FIRST_PHONE = "PREF_FIRST_PHONE"
    private val PREF_FIRST_RULE_USER = "PREF_FIRST_RULE_USER"
    private val PREF_FIRST_RULE_ADMIN = "PREF_FIRST_RULE_ADMIN"
    private val PREF_FIRST_IDKEY = 0
    private val PREF_FIRST_CHECK_DETAIL = 0
    companion object {
        private const val MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val editor = preferences.edit()
    // Lưu lại sign in
    fun setFirstInstall(value: Boolean) {
        editor.putBoolean(PREF_FIRST_INSTALL, value)
        editor.apply()
    }

    fun getFirstInstall(): Boolean {
        return preferences.getBoolean(PREF_FIRST_INSTALL, false)
    }

    // Lưu Phone
    fun setPhoneInstall(value: String?) {
        editor.putString(PREF_FIRST_PHONE, value)
        editor.apply()
    }

    fun getPhoneInstall(): String? {
        return preferences.getString(PREF_FIRST_PHONE, null)
    }

    // Kiểm tra rule User
    fun setRuleUserInstall(value: Boolean) {
        editor.putBoolean(PREF_FIRST_RULE_USER, value)
        editor.apply()
    }

    fun getRuleUserInstall(): Boolean {
        return preferences.getBoolean(PREF_FIRST_RULE_USER, false)
    }

    // Kiểm tra rule Admin
    fun setRuleAdminInstall(value: Boolean) {
        editor.putBoolean(PREF_FIRST_RULE_ADMIN, value)
        editor.apply()
    }

    fun getRuleAdminInstall(): Boolean {
        return preferences.getBoolean(PREF_FIRST_RULE_ADMIN, false)
    }

    fun setKeyIDNotify(isKeyID: Int?) {
        editor.putInt(PREF_FIRST_IDKEY.toString(), isKeyID!!)
        editor.apply()
    }

    fun getKeyIDNotify(): Int {
        return preferences.getInt(PREF_FIRST_IDKEY.toString(), 0)
    }


    // Check back detail --> watchlist or market
    fun setKeyIDCheckDetail(value: Int?) {
        editor.putInt(PREF_FIRST_CHECK_DETAIL.toString(), value!!)
        editor.apply()
    }

    fun getKeyIDCheckDetail(): Int {
        return preferences.getInt(PREF_FIRST_CHECK_DETAIL.toString(),0)
    }
}
