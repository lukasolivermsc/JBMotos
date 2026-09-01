import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { appColors } from '../theme';
import { useFocusEffect } from '@react-navigation/native';
import { useSession } from '../context/SessionContext';
import { getAllSchedulesOrdered, getServiceById, formatDateTime } from '../database/serviceDao';
import { getMotorcycleById } from '../database/motorcycleDao';
import { SafeAreaView } from 'react-native-safe-area-context';

function ScheduleAdminCard({ item }) {
  return (
    <View style={styles.card}>
      <Text style={styles.cardService}>{item.serviceName}</Text>
      <Text style={styles.cardDate}>{item.dateTime}</Text>
      <Text style={styles.cardCustomer}>Cliente: {item.customerUsername}</Text>
      <Text style={styles.cardMoto}>{item.motorcycleBrandModel}</Text>
    </View>
  );
}

export default function HomeAdminScreen({ navigation }) {
  const { user, logout } = useSession();
  const [schedules, setSchedules] = useState([]);

  useFocusEffect(useCallback(() => {
    (async () => {
      const rows = await getAllSchedulesOrdered();
      const enriched = await Promise.all(rows.map(async s => {
        const service = await getServiceById(s.serviceId);
        const motorcycle = await getMotorcycleById(s.motorcycleId);
        return {
          ...s,
          serviceName: service?.name ?? '?',
          dateTime: formatDateTime(s.timestamp),
          customerUsername: motorcycle?.clientUsername ?? '?',
          motorcycleBrandModel: motorcycle ? `${motorcycle.brand} - ${motorcycle.name}` : '?',
        };
      }));
      setSchedules(enriched);
    })();
  }, []));

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.header}>
        <Text style={styles.welcome}>Oi, {user?.name}!</Text>
        <TouchableOpacity style={styles.logoutButton} onPress={() => { navigation.navigate('Services'); logout(); }}>
          <Text style={styles.logoutText}>Sair</Text>
        </TouchableOpacity>
      </View>
      <Text style={styles.sectionTitle}>Agendamentos</Text>
      <FlatList
        data={schedules}
        keyExtractor={item => String(item.id)}
        renderItem={({ item }) => <ScheduleAdminCard item={item} />}
        contentContainerStyle={styles.list}
        ListEmptyComponent={<Text style={styles.empty}>Nenhum agendamento.</Text>}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: appColors.background },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16, backgroundColor: appColors.primary },
  welcome: { fontSize: 18, fontWeight: 'bold', color: appColors.textLight },
  logoutButton: { backgroundColor: appColors.overlayLight, paddingHorizontal: 12, paddingVertical: 6, borderRadius: 8 },
  logoutText: { color: appColors.textLight, fontWeight: 'bold' },
  sectionTitle: { fontSize: 16, fontWeight: '700', color: appColors.textSecondary, margin: 16 },
  list: { paddingHorizontal: 16, paddingBottom: 32 },
  card: { backgroundColor: appColors.card, borderRadius: 10, padding: 14, marginBottom: 10, elevation: 2, borderLeftWidth: 4, borderLeftColor: appColors.borderStrong },
  cardService: { fontSize: 16, fontWeight: 'bold', color: appColors.textSecondary },
  cardDate: { fontSize: 13, color: appColors.textMuted, marginTop: 2 },
  cardCustomer: { fontSize: 13, color: appColors.textLight, marginTop: 4 },
  cardMoto: { fontSize: 13, color: appColors.textLight },
  empty: { textAlign: 'center', color: appColors.empty, marginTop: 40, fontStyle: 'italic' },
});
