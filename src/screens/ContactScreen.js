import React, { useEffect, useState } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, Linking, Platform, ActivityIndicator,
} from 'react-native';
import { appColors } from '../theme';
import MapView, { Marker } from 'react-native-maps';
import { SafeAreaView } from 'react-native-safe-area-context';

const LOCATION = {
  latitude: -23.6572239,
  longitude: -46.7586347,
  latitudeDelta: 0.01,
  longitudeDelta: 0.01,
};

const PHONE = '(11) 98422-3631';

export default function ContactScreen() {
  const [showMap, setShowMap] = useState(false);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setShowMap(true);
    }, 100);

    return () => clearTimeout(timeout);
  }, []);

  function handleCall() {
    const phone = Platform.OS === 'android'
      ? `tel:${PHONE}`
      : `telprompt:${PHONE}`;

    Linking.openURL(phone);
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Contato</Text>
      </View>

      {!showMap ? (
        <View style={styles.loading}>
          <ActivityIndicator size="large" color={appColors.primaryLight} />
        </View>
      ) : (
        <MapView style={styles.map} initialRegion={LOCATION}>
          <Marker coordinate={LOCATION} title="JB Motos" />
        </MapView>
      )}

      <View style={styles.info}>
        <Text style={styles.label}>Telefone</Text>

        <TouchableOpacity style={styles.phoneButton} onPress={handleCall}>
          <Text style={styles.phoneText}>📞 {PHONE}</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: appColors.background },
  header: {
    padding: 16,
    backgroundColor: appColors.primary,
  },
  headerTitle: { fontSize: 20, fontWeight: 'bold', color: appColors.textLight },
  map: { flex: 1 },
  info: {
    padding: 20,
    backgroundColor: appColors.background,
    elevation: 4,
  },
  label: { fontSize: 13, color: appColors.textMuted, marginBottom: 6 },
  phoneButton: {
    backgroundColor: appColors.primary,
    padding: 14,
    borderRadius: 10,
    alignItems: 'center',
  },
  phoneText: { color: appColors.textLight, fontWeight: 'bold', fontSize: 16 },
});
