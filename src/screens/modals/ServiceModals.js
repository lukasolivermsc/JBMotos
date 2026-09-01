import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { appColors } from '../../theme';
import { Picker } from '@react-native-picker/picker';
import { insertService, updateService, deleteService, getServiceById } from '../../database/serviceDao';

const HOURS = Array.from({ length: 13 }, (_, i) => i);
const MINUTES = Array.from({ length: 12 }, (_, i) => i * 5);

function formatCurrencyInput(raw) {
  const digits = raw.replace(/\D/g, '');
  if (!digits) return '';
  const cents = parseInt(digits, 10);
  return `R$ ${Math.floor(cents / 100)},${String(cents % 100).padStart(2, '0')}`;
}

function parseCents(formatted) {
  const digits = formatted.replace(/\D/g, '');
  return digits ? parseInt(digits, 10) : 0;
}

function ServiceForm({ initialName = '', initialPriceCents = 0, initialDuration = 0, onSave, onDelete }) {
  const [name, setName] = useState(initialName);
  const [priceFormatted, setPriceFormatted] = useState(
    initialPriceCents > 0 ? formatCurrencyInput(String(initialPriceCents)) : ''
  );
  const [hours, setHours] = useState(Math.floor(initialDuration / 60));
  const [minutes, setMinutes] = useState(initialDuration % 60);

  const canSave = name.trim().length > 0 && parseCents(priceFormatted) > 0 && (hours * 60 + minutes) > 0;

  return (
    <ScrollView showsVerticalScrollIndicator={false}>
      <Text style={styles.label}>Nome do serviço</Text>
      <TextInput style={styles.input} placeholder="Ex: Troca de óleo" value={name} onChangeText={setName} />

      <Text style={styles.label}>Preço</Text>
      <TextInput style={styles.input} placeholder="R$ 0,00"
        value={priceFormatted} onChangeText={t => setPriceFormatted(formatCurrencyInput(t))}
        keyboardType="numeric" />

      <Text style={styles.label}>Duração</Text>
      <View style={styles.durationRow}>
        <View style={styles.pickerWrapper}>
          <Text style={styles.pickerLabel}>Horas</Text>
          <Picker selectedValue={hours} onValueChange={setHours} style={styles.picker}>
            {HOURS.map(h => <Picker.Item key={h} label={String(h)} value={h} />)}
          </Picker>
        </View>
        <View style={styles.pickerWrapper}>
          <Text style={styles.pickerLabel}>Minutos</Text>
          <Picker selectedValue={minutes} onValueChange={setMinutes} style={styles.picker}>
            {MINUTES.map(m => <Picker.Item key={m} label={String(m)} value={m} />)}
          </Picker>
        </View>
      </View>

      <TouchableOpacity style={[styles.saveButton, !canSave && styles.disabled]}
        onPress={() => onSave({ name: name.trim(), priceInCents: parseCents(priceFormatted), totalMinutes: hours * 60 + minutes })}
        disabled={!canSave}>
        <Text style={styles.saveButtonText}>Salvar</Text>
      </TouchableOpacity>

      {onDelete && (
        <TouchableOpacity style={styles.deleteButton} onPress={onDelete}>
          <Text style={styles.deleteButtonText}>Excluir serviço</Text>
        </TouchableOpacity>
      )}
    </ScrollView>
  );
}

export function AddServiceModal({ navigation, route }) {
  const { onAdded } = route.params ?? {};

  async function handleSave({ name, priceInCents, totalMinutes }) {
    await insertService({ name, price: priceInCents, duration: totalMinutes });
    onAdded?.();
    navigation.goBack();
  }

  return (
    <View style={styles.overlay}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.card}>
        <TouchableOpacity style={styles.closeButton} onPress={() => navigation.goBack()}>
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Novo Serviço</Text>
        <ServiceForm onSave={handleSave} />
      </KeyboardAvoidingView>
    </View>
  );
}

export function EditServiceModal({ navigation, route }) {
  const { serviceId, onSaved } = route.params ?? {};
  const [service, setService] = useState(null);

  useEffect(() => {
    getServiceById(serviceId).then(setService);
  }, [serviceId]);

  async function handleSave({ name, priceInCents, totalMinutes }) {
    await updateService({ id: serviceId, name, price: priceInCents, duration: totalMinutes });
    onSaved?.();
    navigation.goBack();
  }

  async function handleDelete() {
    await deleteService(serviceId);
    onSaved?.();
    navigation.goBack();
  }

  if (!service) return null;

  return (
    <View style={styles.overlay}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.card}>
        <TouchableOpacity style={styles.closeButton} onPress={() => navigation.goBack()}>
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Editar Serviço</Text>
        <ServiceForm
          initialName={service.name}
          initialPriceCents={service.price}
          initialDuration={service.duration}
          onSave={handleSave}
          onDelete={handleDelete}
        />
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: appColors.overlay, justifyContent: 'center', alignItems: 'center' },
  card: { width: '90%', backgroundColor: appColors.textLight, borderRadius: 16, padding: 24, maxHeight: '85%' },
  closeButton: { position: 'absolute', top: 12, right: 16, padding: 4, zIndex: 1 },
  closeText: { fontSize: 18, color: appColors.textSecondary },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 16, color: appColors.textPrimary },
  label: { fontSize: 13, fontWeight: '600', color: appColors.textSecondary, marginTop: 12, marginBottom: 4 },
  input: { borderWidth: 1, borderColor: appColors.border, borderRadius: 8, paddingHorizontal: 12, paddingVertical: 10, fontSize: 16 },
  durationRow: { flexDirection: 'row', gap: 12 },
  pickerWrapper: { flex: 1, borderWidth: 1, borderColor: appColors.border, borderRadius: 8, overflow: 'hidden' },
  pickerLabel: { fontSize: 11, color: appColors.textMuted, textAlign: 'center', paddingTop: 4 },
  picker: { height: 50 },
  saveButton: { backgroundColor: appColors.primary, borderRadius: 8, paddingVertical: 12, alignItems: 'center', marginTop: 20 },
  disabled: { backgroundColor: appColors.border },
  saveButtonText: { color: appColors.textLight, fontWeight: 'bold', fontSize: 16 },
  deleteButton: { borderWidth: 1, borderColor: appColors.primary, borderRadius: 8, paddingVertical: 12, alignItems: 'center', marginTop: 10 },
  deleteButtonText: { color: appColors.primary, fontWeight: 'bold', fontSize: 16 },
});
