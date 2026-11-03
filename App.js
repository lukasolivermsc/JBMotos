import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator } from 'react-native';
import { SessionProvider } from './src/context/SessionContext';
import AppNavigator from './src/navigation/AppNavigator';
import { getDB } from './src/database/db';

import { SafeAreaProvider } from 'react-native-safe-area-context';

import { appColors } from './src/theme';

export default function App() {
  const [dbReady, setDbReady] = useState(false);
  const [dbError, setDbError] = useState(false);

  useEffect(() => {
    getDB()
      .then(() => setDbReady(true))
      .catch(e => {
        console.error('Erro ao abrir o banco de dados:', e);
        setDbError(true);
      });
  }, []);

  if (dbError) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', padding: 24 }}>
        <Text>Não foi possível abrir o banco de dados.</Text>
      </View>
    );
  }

  if (!dbReady) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color={appColors.primary} />
      </View>
    );
  }

  return (
    <SafeAreaProvider>
      <SessionProvider>
        <AppNavigator />
      </SessionProvider>
    </SafeAreaProvider>
  );
}
