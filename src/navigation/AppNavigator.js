import React from 'react';
import { Image } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
 
import { useSession } from '../context/SessionContext';
 
import HomeScreen from '../screens/HomeScreen';
import HomeAdminScreen from '../screens/HomeAdminScreen';
import ServicesScreen from '../screens/ServicesScreen';
import ServicesAdminScreen from '../screens/ServicesAdminScreen';
import ContactScreen from '../screens/ContactScreen';
 
import LoginModal from '../screens/modals/LoginModal';
import ServiceDetailModal from '../screens/modals/ServiceDetailModal';
import AddMotorcycleModal from '../screens/modals/AddMotorcycleModal';
import { AddServiceModal, EditServiceModal } from '../screens/modals/ServiceModals';
import { appColors } from '../theme';
 
const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();
 
function HomeTabs() {
  const { isLoggedIn, isAdmin } = useSession();
 
  return (
    <Tab.Navigator
      initialRouteName="Services"
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: appColors.background,
          borderTopColor: appColors.borderStrong
        },
        tabBarActiveTintColor: appColors.secondary,
      }}
    >
      <Tab.Screen
        name="Home"
        component={isAdmin ? HomeAdminScreen : HomeScreen}
        options={{
          tabBarLabel: 'Home',
          tabBarIcon: ({ focused }) => (
            <Image
              source={require('../assets/iconBike.png')}
              style={{
                width: 32,
                height: 32,
                tintColor: focused ? appColors.secondary : appColors.textMuted,
              }}
            />
          ),
        }}
        listeners={({ navigation }) => ({
          tabPress: (e) => {
            if (!isLoggedIn) {
              e.preventDefault();

              navigation.navigate('LoginModal');
            }
          },
        })}
      />
      <Tab.Screen
        name="Services"
        component={isAdmin ? ServicesAdminScreen : ServicesScreen}
        options={{
          tabBarLabel: 'Serviços',
          tabBarIcon: ({ focused }) => (
            <Image
              source={require('../assets/iconCog.png')}
              style={{
                width: 24,
                height: 24,
                tintColor: focused ? appColors.secondary : appColors.textMuted,
              }}
            />
          ),
        }}
      />
      <Tab.Screen
        name="Contact"
        component={ContactScreen}
        options={{
          tabBarLabel: 'Contato',
          tabBarIcon: ({ focused }) => (
            <Image
              source={require('../assets/iconPhone.png')}
              style={{
                width: 24,
                height: 24,
                tintColor: focused ? appColors.secondary : appColors.textMuted,
              }}
            />
          ),
        }}
      />
    </Tab.Navigator>
  );
}
 
export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
 
        <Stack.Screen name="Main" component={HomeTabs} />
 
        <Stack.Screen
          name="LoginModal"
          component={LoginModal}
          options={{ presentation: 'modal' }}
        />
        <Stack.Screen
          name="ServiceDetailModal"
          component={ServiceDetailModal}
          options={{ presentation: 'modal' }}
        />
        <Stack.Screen
          name="AddMotorcycleModal"
          component={AddMotorcycleModal}
          options={{ presentation: 'modal' }}
        />
        <Stack.Screen
          name="AddServiceModal"
          component={AddServiceModal}
          options={{ presentation: 'modal' }}
        />
        <Stack.Screen
          name="EditServiceModal"
          component={EditServiceModal}
          options={{ presentation: 'modal' }}
        />
 
      </Stack.Navigator>
    </NavigationContainer>
  );
}
