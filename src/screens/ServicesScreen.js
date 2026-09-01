import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { getAllServices, formatPrice } from '../database/serviceDao';
import { SafeAreaView } from 'react-native-safe-area-context';
import { appColors } from '../theme';
import { useSession } from '../context/SessionContext';

function ServiceCard({ service, onPress }) {
  return (
    <View style={styles.card}>
      <View style={styles.cardInfo}>
        <Text style={styles.cardName}>{service.name}</Text>
        <Text style={styles.cardPrice}>{formatPrice(service.price)}</Text>
      </View>
      <TouchableOpacity style={styles.openButton} onPress={() => onPress(service)}>
        <Text style={styles.openButtonText}>Ver</Text>
      </TouchableOpacity>
    </View>
  );
}

export default function ServicesScreen({ navigation }) {
  const [services, setServices] = useState([]);
  const { isLoggedIn } = useSession();

  useFocusEffect(useCallback(() => {
    getAllServices().then(setServices);
  }, []));

  function handleServicePress(service) {
    if (!isLoggedIn) {
      navigation.navigate('LoginModal');
      return;
    }
    navigation.navigate('ServiceDetailModal', {
      service,
      onScheduled: undefined,
    });
  }

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Serviços</Text>
      </View>
      <FlatList
        data={services}
        keyExtractor={item => String(item.id)}
        renderItem={({ item }) => (
          <ServiceCard service={item} onPress={handleServicePress} />
        )}
        contentContainerStyle={styles.list}
        contentInsetAdjustmentBehavior="automatic"
        ListEmptyComponent={<Text style={styles.empty}>Nenhum serviço disponível.</Text>}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: appColors.background },
  header: { padding: 16, backgroundColor: appColors.primary },
  headerTitle: { fontSize: 20, fontWeight: 'bold', color: appColors.textLight },
  list: { padding: 16, paddingBottom: 64 },
  card: { backgroundColor: appColors.card, borderRadius: 10, padding: 14, marginBottom: 10, elevation: 2, flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  cardInfo: { flex: 1 },
  cardName: { fontSize: 16, fontWeight: '600', color: appColors.textPrimary },
  cardPrice: { fontSize: 14, color: appColors.primary, marginTop: 2 },
  openButton: { backgroundColor: appColors.primary, paddingHorizontal: 16, paddingVertical: 8, borderRadius: 8, marginLeft: 12 },
  openButtonText: { color: appColors.textLight, fontWeight: 'bold' },
  empty: { textAlign: 'center', color: appColors.empty, marginTop: 40, fontStyle: 'italic' },
});
