package com.example.testapi.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.testapi.Fragment.HomeAdminFragment
import com.example.testapi.Fragment.ListAdminFragment
import com.example.testapi.Fragment.ListUserFragment

class HomeAdminViewAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeAdminFragment()
            1 -> ListAdminFragment()
            2 -> ListUserFragment()
            else -> HomeAdminFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Home"
            1 -> "List Admin"
            2 -> "List User"
            else -> "Home"
        }
    }
}
