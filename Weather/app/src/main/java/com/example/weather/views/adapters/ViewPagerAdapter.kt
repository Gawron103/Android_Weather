package com.example.weather.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragments: ArrayList<Fragment>,
    manager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(manager, lifecycle) {

    private val fragmentsList = fragments

    override fun getItemCount(): Int {
        return fragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }
}