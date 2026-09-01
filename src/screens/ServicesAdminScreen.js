import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { appColors } from '../theme';
import { useFocusEffect } from '@react-navigation/native';
import { getAllServices, formatPrice } from '../database/serviceDao';
import { SafeAreaView } from 'react-native-safe-area-context';

function ServiceAdminCard({ service, onPress }) {
  return (
    <TouchableOpacity style={styles.card} onPress={() => onPress(service)}>
      <Text style={styles.cardName}>{service.name}</Text>
      <Text style={styles.cardPrice}>{formatPrice(service.price)}</Text>
      <Text style={styles.cardDuration}>{service.duration} min</Text>
    </TouchableOpacity>
  );
}

export default function ServicesAdminScreen({ navigation }) {
  const [services, setServices] = useState([]);

  async function loadServices() {
    setServices(await getAllServices());
  }

  useFocusEffect(useCallback(() => { loadServices(); }, []));

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Serviços</Text>
        <TouchableOpacity style={styles.addButton}
          onPress={() => navigation.navigate('AddServiceModal', { onAdded: loadServices })}>
          <Text style={styles.addButtonText}>+ Adicionar</Text>
        </TouchableOpacity>
      </View>
      <FlatList
        data={services}
        keyExtractor={item => String(item.id)}
        renderItem={({ item }) => (
          <ServiceAdminCard service={item}
            onPress={s => navigation.navigate('EditServiceModal', { serviceId: s.id, onSaved: loadServices })} />
        )}
        contentContainerStyle={styles.list}
        ListEmptyComponent={<Text style={styles.empty}>Nenhum serviço cadastrado.</Text>}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: appColors.background },
  header: { padding: 16, backgroundColor: appColors.primary, flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  headerTitle: { fontSize: 20, fontWeight: 'bold', color: appColors.textLight },
  addButton: { backgroundColor: appColors.overlayLight, paddingHorizontal: 12, paddingVertical: 6, borderRadius: 8 },
  addButtonText: { color: appColors.textLight, fontWeight: 'bold' },
  list: { padding: 16 },
  card: { backgroundColor: appColors.card, borderRadius: 10, padding: 14, marginBottom: 10, elevation: 2, borderLeftWidth: 4, borderLeftColor: appColors.borderStrong },
  cardName: { fontSize: 16, fontWeight: '600', color: appColors.textPrimary },
  cardPrice: { fontSize: 14, color: appColors.textSecondary, marginTop: 2 },
  cardDuration: { fontSize: 12, color: appColors.textMuted, marginTop: 2 },
  empty: { textAlign: 'center', color: appColors.empty, marginTop: 40, fontStyle: 'italic' },
});
