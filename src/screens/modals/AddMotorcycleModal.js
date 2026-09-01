import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform } from 'react-native';
import { appColors } from '../../theme';
import { useSession } from '../../context/SessionContext';
import { insertMotorcycle } from '../../database/motorcycleDao';

export default function AddMotorcycleModal({ navigation, route }) {
  const [name, setName] = useState('');
  const [brand, setBrand] = useState('');
  const { user } = useSession();
  const { onAdded } = route.params ?? {};

  async function handleSave() {
    if (!name.trim() || !brand.trim()) return;
    await insertMotorcycle({ name: name.trim(), brand: brand.trim(), clientUsername: user.name });
    onAdded?.();
    navigation.goBack();
  }

  return (
    <View style={styles.overlay}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.card}>
        <TouchableOpacity style={styles.closeButton} onPress={() => navigation.goBack()}>
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Adicionar Moto</Text>
        <TextInput style={styles.input} placeholder="Modelo (ex: CB 300)" value={name} onChangeText={setName} />
        <TextInput style={styles.input} placeholder="Marca (ex: Honda)" value={brand} onChangeText={setBrand} />
        <TouchableOpacity
          style={[styles.saveButton, (!name.trim() || !brand.trim()) && styles.disabled]}
          onPress={handleSave} disabled={!name.trim() || !brand.trim()}>
          <Text style={styles.saveButtonText}>Salvar</Text>
        </TouchableOpacity>
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: appColors.overlay, justifyContent: 'center', alignItems: 'center' },
  card: { width: '85%', backgroundColor: appColors.textLight, borderRadius: 16, padding: 24 },
  closeButton: { position: 'absolute', top: 12, right: 16, padding: 4 },
  closeText: { fontSize: 18, color: appColors.textSecondary },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 20, color: appColors.textPrimary },
  input: { borderWidth: 1, borderColor: appColors.border, borderRadius: 8, paddingHorizontal: 12, paddingVertical: 10, fontSize: 16, marginBottom: 12 },
  saveButton: { backgroundColor: appColors.primary, borderRadius: 8, paddingVertical: 12, alignItems: 'center', marginTop: 4 },
  disabled: { backgroundColor: appColors.border },
  saveButtonText: { color: appColors.textLight, fontWeight: 'bold', fontSize: 16 },
});
