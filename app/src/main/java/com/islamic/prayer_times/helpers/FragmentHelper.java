package com.islamic.prayer_times.helpers;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;


/**
 * Created by Net22 on 1/30/2018.
 */

public class FragmentHelper {

    private Fragment currentFragment;

    FragmentManager supportFragmentManager;
    private ArrayList<Fragment> fragmentStack;
    private ArrayList<String> fragmentStackNames;
    private int fragmentContainerId;
    private ActivityInterface activityInterface;

    public FragmentHelper(FragmentManager supportFragmentManager, int fragmentContainerId
            , ActivityInterface activityInterface) {
        this.supportFragmentManager = supportFragmentManager;
        this.fragmentContainerId = fragmentContainerId;
        this.activityInterface = null;
        this.activityInterface = activityInterface;
        fragmentStack = new ArrayList<Fragment>();
        fragmentStackNames = new ArrayList<>();
    }


    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }


    public int getFragmentContainerId() {
        return fragmentContainerId;
    }

    public void setFragmentContainerId(int fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
    }


    public void setSupportFragmentManager(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
    }

    public ArrayList<Fragment> getFragmentStack() {
        return fragmentStack;
    }


    public ArrayList<String> getFragmentStackNames() {
        return fragmentStackNames;
    }

    public void showFragment(Fragment fragment,boolean reCreate) {
        String fragmentTag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentStack.size() > 0) {
            if (!fragmentStack.get(fragmentStack.size() - 1).getClass().getName().equals(fragmentTag)) {
                fragmentStack.get(fragmentStack.size() - 1).onPause();
                ft.hide(fragmentStack.get(fragmentStack.size() - 1));
            }
        }

        if (!fragmentStackNames.contains(fragmentTag)) {
            //block add fragment
            ft.add(fragmentContainerId, fragment, fragmentTag);
            fragmentStackNames.add(fragmentTag);
            fragmentStack.add(fragment);
        } else {

            {
                reCreateFragment(ft, fragment,reCreate);
                return;
            }
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        setCurrentFragment(fragment);
    }

    public void showFragmentWithRepeat(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentStack.size() > 0) {

            fragmentStack.get(fragmentStack.size() - 1).onPause();
            ft.hide(fragmentStack.get(fragmentStack.size() - 1));

        }
        //block add fragment
        ft.add(fragmentContainerId, fragment, fragmentTag);
        fragmentStackNames.add(fragmentTag);
        fragmentStack.add(fragment);
        //
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        setCurrentFragment(fragment);
    }

    public void reOpenLastFragment(boolean reCreate) {
        Fragment fragment = getCurrentFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        reCreateFragment(ft, fragment,reCreate);
    }


    private void reCreateFragment(FragmentTransaction ft, Fragment fragment,boolean reCreate) {
        String fragmentTag = fragment.getClass().getName();
        fragmentStackNames.remove(fragmentTag);
        int indexFragmentInStack = 0;
        Fragment fragmentInStack = new Fragment();
        for (int i = fragmentStack.size() - 1; i >= 0; i--) {
            if (fragmentStack.get(i).getClass().getName().equals(fragmentTag)) {
                indexFragmentInStack = i;
                fragmentInStack = fragmentStack.get(i);
                break;
            }
        }

        if (fragmentStack.size() > indexFragmentInStack) {
            fragmentStack.remove(indexFragmentInStack);
        }

        if(reCreate)
        {
            //  start first method remove from stack and create new one

            ft.remove(fragmentInStack);
            ft.add(fragmentContainerId, fragment, fragmentTag);
            fragmentStackNames.add(fragmentTag);
            fragmentStack.add(fragment);

            //end  first method remove from stack and create new one
        }
        else
        {
            //start second method resume from stack
            if(fragmentInStack.isAdded())
            {
                ft.show(fragmentInStack);
                fragmentInStack.onResume();
            }
            else
            {
                ft.add(fragmentContainerId, fragmentInStack, fragmentTag);
            }

            fragmentStackNames.add(fragmentTag);
            fragmentStack.add(fragmentInStack);

            //end second  method resume from stack
        }


        ft.commit();
        setCurrentFragment(fragment);
    }


    public String backFragmentsTag() {
        backFragments();
        if(getCurrentFragment()!=null)
        {
            return   getCurrentFragment().getClass()
                    .getName();
        }
        return "";
    }


    public void backFragments() {
        if (fragmentStack.size() >= 2) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragmentStack.get(fragmentStack.size() - 1).onPause();
            fragmentStackNames.remove(fragmentStackNames.size() - 1);
            ft.remove(fragmentStack.get(fragmentStack.size() - 1));
            fragmentStack.remove(fragmentStack.size() - 1);

            if (fragmentStack.get(fragmentStack.size() - 1).isAdded()) {
                ft.show(fragmentStack.get(fragmentStack.size() - 1));
            } else {
                ft.add(fragmentContainerId, fragmentStack.get(fragmentStack.size() - 1)
                        , fragmentStack.get(fragmentStack.size() - 1).getClass()
                                .getName());
            }
            ft.commit();
            setCurrentFragment(fragmentStack.get(fragmentStack.size() - 1));
            if (fragmentStack.get(fragmentStack.size() - 1).isAdded()) {
                fragmentStack.get(fragmentStack.size() - 1).onResume();
            }

            activityInterface.switchActivityUIByFragment(fragmentStack.get(fragmentStack.size() - 1));

        } else {
            activityInterface.noBackFragments();
        }
    }


    public void removeAllFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = fragmentStack.size() - 1; i >= 0; i--) {
            String fragmentTag = fragmentStack.get(i).getClass().getName();
            fragmentStackNames.remove(fragmentTag);
            ft.remove(fragmentStack.get(i));
            fragmentStack.remove(i);
        }
        ft.commit();
    }

    public void removeAllFragmentsExceptOne(String fragmentTag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = fragmentStack.size() - 1; i >= 0; i--) {
            if (!fragmentTag.equals(fragmentStack.get(i).getClass().getName())) {
                String toBeRemovfragmentTag = fragmentStack.get(i).getClass().getName();
                fragmentStackNames.remove(toBeRemovfragmentTag);
                ft.remove(fragmentStack.get(i));
                fragmentStack.remove(i);
            }
        }
        ft.commit();
    }

    public void removeFragment(String fragmentTag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = fragmentStackNames.size() - 1; i >= 0; i--) {
            if (fragmentTag.equals(fragmentStackNames.get(i))) {
                fragmentStackNames.remove(fragmentTag);
            }
        }
        for (int i = fragmentStack.size() - 1; i >= 0; i--) {
            if (fragmentStack.get(i).getClass().getName().equals(fragmentTag)) {

                ft.remove(fragmentStack.get(i));
                fragmentStack.remove(i);
            }
        }
        ft.commit();

    }

    public void removeFragment(Fragment fragment) {
        removeFragment(fragment.getClass().getName());
    }

    public void removeFragment(Fragment fragment, int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragmentStackNames.remove(index);
        fragmentStack.remove(index);
        ft.remove(fragment);
        ft.commit();
    }

    public interface ActivityInterface {
        public void switchActivityUIByFragment(Fragment fragment);

        public void noBackFragments();
    }
}
