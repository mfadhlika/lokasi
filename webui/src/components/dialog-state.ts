export type DialogState<T> = {
    isOpen: boolean;
    toggleModal: () => void;
    data: T | null;
    setData: (object: T) => void;
}
