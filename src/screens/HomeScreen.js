import React, { useState, useRef, useCallback, useEffect } from 'react';
import {
  View, Text, FlatList, TouchableOpacity,
  StyleSheet, Dimensions, ScrollView, Alert,
} from 'react-native';
import { generateAndShareSchedulePdf } from '../utils/schedulePdf';
import { appColors } from '../theme';
import { useFocusEffect } from '@react-navigation/native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useSession } from '../context/SessionContext';
import { getMotorcyclesByUsername, deleteMotorcycle } from '../database/motorcycleDao';
import { getSchedulesByMotorcycleId, getServiceById, formatDateTime } from '../database/serviceDao';

const { width } = Dimensions.get('window');

function MotorcycleCard({ motorcycle, refreshKey }) {
  const [schedules, setSchedules] = useState([]);

  useEffect(() => {
    (async () => {
      const rows = await getSchedulesByMotorcycleId(motorcycle.id);
      const enriched = await Promise.all(
        rows.map(async s => {
          const service = await getServiceById(s.serviceId);
          return { ...s, serviceName: service?.name ?? '?' };
        })
      );
      setSchedules(enriched);
    })();
  }, [motorcycle.id, refreshKey]);

  return (
    <View style={styles.motorcycleCard}>
      <Text style={styles.motorcycleName}>{motorcycle.name}</Text>
      <Text style={styles.motorcycleBrand}>{motorcycle.brand}</Text>
      <Text style={styles.scheduleTitle}>Agendamentos</Text>
      <ScrollView>
        {schedules.length === 0 ? (
          <Text style={styles.scheduleEmpty}>Nada agendado</Text>
        ) : (
          schedules.map(s => (
            <View key={s.id} style={styles.scheduleItem}>
              <Text style={styles.scheduleServiceName}>{s.serviceName}</Text>
              <Text style={styles.scheduleDate}>{formatDateTime(s.timestamp)}</Text>
            </View>
          ))
        )}
      </ScrollView>
    </View>
  );
}

export default function HomeScreen({ navigation, route }) {
  const { user, logout } = useSession();
  const [generatingPdf, setGeneratingPdf] = useState(false);
  const focusMotorcycleId = route?.params?.focusMotorcycleId;
  const [motorcycles, setMotorcycles] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [scheduleRefreshKey, setScheduleRefreshKey] = useState(0);
  const flatListRef = useRef(null);

  async function loadMotorcycles() {
    const list = await getMotorcyclesByUsername(user?.name);
    setMotorcycles(list);
    return list;
  }

  // Depois de agendar, mostra a moto que acabou de ser agendada
  useEffect(() => {
    if (focusMotorcycleId == null) return;
    (async () => {
      const list = await loadMotorcycles();
      const index = list.findIndex(m => m.id === focusMotorcycleId);
      if (index >= 0) {
        setCurrentIndex(index);
        setTimeout(() => {
          flatListRef.current?.scrollToOffset({ offset: index * width, animated: false });
        }, 50);
      }
      navigation.setParams({ focusMotorcycleId: undefined });
    })();
  }, [focusMotorcycleId]);

  async function handleGeneratePdf() {
    const motorcycle = motorcycles[currentIndex];
    if (!motorcycle || generatingPdf) return;
    setGeneratingPdf(true);
    try {
      const rows = await getSchedulesByMotorcycleId(motorcycle.id);
      if (rows.length === 0) return;
      const schedules = await Promise.all(
        rows.map(async s => {
          const service = await getServiceById(s.serviceId);
          return {
            ...s,
            serviceName: service?.name ?? '?',
            price: service?.price ?? 0,
            duration: service?.duration ?? 0,
          };
        })
      );
      await generateAndShareSchedulePdf({ user, motorcycle, schedules });
    } catch (e) {
      Alert.alert('Erro', 'Não foi possível gerar o PDF.');
    } finally {
      setGeneratingPdf(false);
    }
  }

  useFocusEffect(useCallback(() => {
    loadMotorcycles();
    setScheduleRefreshKey(k => k + 1);
  }, [user]));

  async function handleRemoveMotorcycle() {
    if (motorcycles.length === 0) return;
    await deleteMotorcycle(motorcycles[currentIndex].id);
    setCurrentIndex(i => Math.max(0, i - 1));
    loadMotorcycles();
  }

  const displayList = motorcycles.length > 0 ? motorcycles : [{ id: -1, name: '', brand: '' }];

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.welcome}>Oi, {user?.name}!</Text>
        <TouchableOpacity style={styles.logoutButton} onPress={() => { navigation.navigate('Services'); logout(); }}>
          <Text style={styles.logoutText}>Sair</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        ref={flatListRef}
        data={displayList}
        keyExtractor={item => String(item.id)}
        horizontal pagingEnabled showsHorizontalScrollIndicator={false}
        renderItem={({ item }) => (
          <MotorcycleCard motorcycle={item} refreshKey={scheduleRefreshKey} />
        )}
        onMomentumScrollEnd={e => {
          setCurrentIndex(Math.round(e.nativeEvent.contentOffset.x / width));
        }}
      />

      <View style={styles.indicators}>
        {displayList.map((_, i) => (
          <View key={i} style={[styles.dot, i === currentIndex && styles.dotActive]} />
        ))}
      </View>

      <View style={styles.motorcycleActions}>
        <TouchableOpacity style={styles.actionButton}
          onPress={() => navigation.navigate('AddMotorcycleModal')}>
          <Text style={styles.actionButtonText}>{'➕\nAdicionar\nMoto'}</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.actionButton, styles.actionButtonDanger]}
          onPress={handleRemoveMotorcycle} disabled={motorcycles.length === 0}>
          <Text style={styles.actionButtonText}>{'🗑\nRemover\nMoto'}</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.actionButton}
          onPress={handleGeneratePdf} disabled={motorcycles.length === 0 || generatingPdf}>
          <Text style={styles.actionButtonText}>{generatingPdf ? '📄\nGerando\n...' : '📄\nGerar\nPDF'}</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: appColors.background },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16, backgroundColor: appColors.primary },
  welcome: { fontSize: 18, fontWeight: 'bold', color: appColors.textLight },
  logoutButton: { backgroundColor: appColors.overlayLight, paddingHorizontal: 12, paddingVertical: 6, borderRadius: 8 },
  logoutText: { color: appColors.textLight, fontWeight: 'bold' },
  motorcycleCard: { width: width - 32, marginHorizontal: 16, backgroundColor: appColors.card, borderRadius: 12, padding: 16, marginTop: 16, elevation: 2 },
  motorcycleName: { fontSize: 20, fontWeight: 'bold', color: appColors.textPrimary },
  motorcycleBrand: { fontSize: 14, color: appColors.textMuted, marginBottom: 12 },
  scheduleTitle: { fontSize: 14, fontWeight: '600', color: appColors.textSecondary, marginBottom: 8 },
  scheduleEmpty: { color: appColors.empty, fontStyle: 'italic' },
  scheduleItem: { borderLeftWidth: 3, borderLeftColor: appColors.borderStrong, paddingLeft: 8, marginBottom: 6 },
  scheduleServiceName: { fontSize: 14, fontWeight: '600', color: appColors.textSecondary },
  scheduleDate: { fontSize: 12, color: appColors.textMuted },
  indicators: { flexDirection: 'row', justifyContent: 'center', marginTop: 12 },
  dot: { width: 8, height: 8, borderRadius: 4, backgroundColor: appColors.border, marginHorizontal: 4 },
  dotActive: { backgroundColor: appColors.primary },
  motorcycleActions: { flexDirection: 'row', justifyContent: 'center', gap: 12, marginTop: 16, paddingHorizontal: 16 },
  actionButton: { flex: 1, backgroundColor: appColors.primary, padding: 12, borderRadius: 8, alignItems: 'center' },
  actionButtonDanger: { backgroundColor: appColors.primaryDark },
  actionButtonText: { color: appColors.textLight, fontWeight: 'bold', textAlign: 'center' },
});