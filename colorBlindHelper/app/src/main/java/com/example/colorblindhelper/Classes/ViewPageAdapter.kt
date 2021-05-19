package com.example.colorblindhelper.Classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.colorblindhelper.ui.Tabs.LiveChangeFragment
import com.example.colorblindhelper.ui.Tabs.searchUser
import com.example.colorblindhelper.ui.Tabs.Profile
import com.example.colorblindhelper.ui.Tabs.uploadImageFragment

class ViewPageAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

    var mCountTabs = behavior
    init{
        this.mCountTabs = behavior
    }
    override fun getCount(): Int {
        return mCountTabs;
    }
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LiveChangeFragment()
            3 -> uploadImageFragment()
            2 -> Profile()
            1 -> searchUser()
            else -> LiveChangeFragment()
        }
    }
}