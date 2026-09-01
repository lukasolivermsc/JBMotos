import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useSession } from '../../context/SessionContext';

import { appColors } from '../../theme';

export default function LoginModal({ navigation }) {
  const [username, setUsername] = useState('');
  const { login } = useSession();

  async function handleLogin() {
    const trimmed = username.trim();
    if (!trimmed) return;
    await login(trimmed);
    navigation.navigate('Main', {screen: 'Home',});
  }

  function handleClose() {
    navigation.goBack();
  }

  return (
    <View style={styles.overlay}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.card}
      >
        <TouchableOpacity style={styles.closeButton} onPress={handleClose}>
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>

        <Text style={styles.title}>Entrar</Text>

        <TextInput
          style={styles.input}
          placeholder="Seu nome de usuário"
          value={username}
          onChangeText={setUsername}
          autoFocus
          returnKeyType="done"
          onSubmitEditing={handleLogin}
        />

        <TouchableOpacity
          style={[styles.loginButton, !username.trim() && styles.loginButtonDisabled]}
          onPress={handleLogin}
          disabled={!username.trim()}
        >
          <Text style={styles.loginButtonText}>Entrar</Text>
        </TouchableOpacity>
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: appColors.overlay,
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    width: '85%',
    backgroundColor: appColors.card,
    borderRadius: 16,
    padding: 24,
  },
  closeButton: {
    position: 'absolute',
    top: 12,
    right: 16,
    padding: 4,
  },
  closeText: {
    fontSize: 18,
    color: appColors.danger,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
    color: appColors.textPrimary,
  },
  input: {
    borderWidth: 1,
    borderColor: appColors.border,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    marginBottom: 16,
    color: appColors.textPrimary,
  },
  loginButton: {
    backgroundColor: appColors.primary,
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
  },
  loginButtonDisabled: {
    backgroundColor: appColors.border,
  },
  loginButtonText: {
    color: appColors.textPrimary,
    fontWeight: 'bold',
    fontSize: 16,
  },
});