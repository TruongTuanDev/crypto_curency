package com.example.testapi.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.testapi.Fragment.HomeAdminFragment
import com.example.testapi.Fragment.ListAdminFragment
import com.example.testapi.Fragment.ListUserFragment
import com.example.testapi.Fragment.ProfileFragment
import com.example.testapi.Fragment.WalletsFragment

class AccountViewAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WalletsFragment()
            1 -> ProfileFragment()
            else -> WalletsFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Wallets"
            1 -> "Profile"
            else -> "Wallets"
        }
    }
}
