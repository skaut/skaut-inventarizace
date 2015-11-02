package cz.skaut.warehousemanager.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.pnikosis.materialishprogress.ProgressWheel
import cz.skaut.warehousemanager.R
import cz.skaut.warehousemanager.WarehouseApplication
import cz.skaut.warehousemanager.entity.Role
import cz.skaut.warehousemanager.helper.C
import cz.skaut.warehousemanager.util.bindView
import me.tatarka.rxloader.RxLoader2
import me.tatarka.rxloader.RxLoaderManagerCompat
import me.tatarka.rxloader.RxLoaderObserver
import timber.log.Timber

class LoginFragment : BaseFragment() {

    val userNameText: EditText by bindView(R.id.userName)
    val passwordText: EditText by bindView(R.id.password)
    val progressWheel: ProgressWheel by bindView(R.id.progressWheel)
    val loginBox: LinearLayout by bindView(R.id.loginBox)
    val loginButton: Button by bindView(R.id.loginButton)

    private var loginLoader: RxLoader2<String, String, List<Role>>? = null

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_login
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideUpButton()
        setTitle(R.string.login_title)

        val prefUserName = WarehouseApplication.getPrefs().getString(C.USER_NAME, "")
        if (!TextUtils.isEmpty(prefUserName)) {
            userNameText.setText(prefUserName)
        }

        val prefUserPassword = WarehouseApplication.getPrefs().getString(C.USER_PASSWORD, "")
        if (!TextUtils.isEmpty(prefUserPassword)) {
            passwordText.setText(prefUserPassword)
        }

        // create login loader
        loginLoader = RxLoaderManagerCompat.get(this).create<String, String, List<Role>>(
                { username, password -> WarehouseApplication.getLoginManager().login(username, password) },
                object : RxLoaderObserver<List<Role>>() {
                    override fun onNext(roles: List<Role>) {
                        Timber.d("got roles: " + roles)
                        if (roles.size == 0) {
                            loginBox.visibility = View.VISIBLE
                            progressWheel.visibility = View.GONE
                            Snackbar.make(view, R.string.login_no_roles, Snackbar.LENGTH_LONG).show()
                        } else {
                            activity.supportFragmentManager.beginTransaction().replace(R.id.container, RoleFragment.newInstance(roles)).commit()
                        }
                    }

                    override fun onStarted() {
                        loginBox.visibility = View.GONE
                        progressWheel.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable?) {
                        Timber.e(e, "Failed to login")
                        loginBox.visibility = View.VISIBLE
                        progressWheel.visibility = View.GONE
                        Snackbar.make(view, R.string.login_failed, Snackbar.LENGTH_LONG).show()
                    }
                })

        loginButton.setOnClickListener(View.OnClickListener {
            val isNameEmpty = TextUtils.isEmpty(userNameText.text)
            val isPasswordEmpty = TextUtils.isEmpty(passwordText.text)

            if (isNameEmpty || isPasswordEmpty) {
                if (isNameEmpty) {
                }
                if (isPasswordEmpty) {
                }
            } else {
                loginLoader!!.restart(userNameText.text.toString(), passwordText.text.toString())
            }
        })
    }
}
